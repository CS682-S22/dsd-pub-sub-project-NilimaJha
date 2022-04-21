package broker;

import com.google.protobuf.Any;
import connection.Connection;
import customeException.ConnectionClosedException;
import model.BrokerInfo;
import proto.FailedMemberInfo;
import util.Constants;
import model.MembershipTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.HeartBeatMessage;
import util.Utility;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * class that performs operations related to heartbeat between members in the membership table.
 * @author nilimajha 
 */
public class HeartBeatModule {
    private static final Logger logger = LogManager.getLogger(RequestProcessor.class);
    private ConcurrentHashMap<Integer, Long> heartbeatReceiveTimes = new ConcurrentHashMap();
    private BrokerInfo thisBrokerInfo;
    private MembershipTable membershipTable = MembershipTable.getMembershipTable(Constants.BROKER);
    private Timer heartbeatCheckTimer;
    private Timer heartbeatSendTimer;
    private static HeartBeatModule heartBeatModule = null;
    private String loadBalancerIp;
    private int loadBalancerPort;

    /**
     * Constructor
     */
    private HeartBeatModule(BrokerInfo thisBrokerInfo, String loadBalancerIp, int loadBalancerPort) {
        this.thisBrokerInfo = thisBrokerInfo;
        this.loadBalancerIp = loadBalancerIp;
        this.loadBalancerPort = loadBalancerPort;
        startHeartbeatCheckTimer(); // starting heartbeatChecker timerTask.
        startHeartbeatSendTimer();  // start heartbeatSender timerTask.
    }

    /**
     *
     * @return
     */
    synchronized static HeartBeatModule getHeartBeatModule(BrokerInfo thisBrokerInfo, String loadBalancerIp, int loadBalancerPort) {
        if (heartBeatModule == null) {
            heartBeatModule = new HeartBeatModule(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
        }
        return heartBeatModule;
    }

    /**
     * timer task to check the Heartbeat time and detect failure.
     */
    private void startHeartbeatCheckTimer() {
        TimerTask timerTask = new TimerTask() {
            public void run() {
                heartbeatCheckTimer.cancel();
                heartbeatCheck();
                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] starting the HB Check timer.");
                startHeartbeatCheckTimer();
            }
        };
        heartbeatCheckTimer = new Timer();
        heartbeatCheckTimer.schedule(timerTask, Constants.HEARTBEAT_CHECK_TIMER_TIMEOUT);
    }

    /**
     * timer task to check the Heartbeat time and detect failure.
     */
    private void startHeartbeatSendTimer() {
        TimerTask timerTask = new TimerTask() {
            public void run() {
                heartbeatSendTimer.cancel();
                heartbeatSend();
                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] starting the HB Send timer.");
                startHeartbeatSendTimer();
            }
        };
        heartbeatSendTimer = new Timer();
        heartbeatSendTimer.schedule(timerTask, Constants.HEARTBEAT_SEND_TIMER_TIMEOUT);
    }

    /**
     * checks the last heartbeat message time and detect failure on the basis of that.
     */
    public void heartbeatCheck() {
        long now = System.nanoTime();
        for (Map.Entry<Integer, Long> set : heartbeatReceiveTimes.entrySet()) {
            long lastHeartbeatReceivedTime = heartbeatReceiveTimes.get(set.getKey());
            long timeSinceLastHeartbeat = now - lastHeartbeatReceivedTime;
            if (timeSinceLastHeartbeat >= Constants.TIMEOUT_NANOS) {
                logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "]  timeSinceLastHeartbeat = " + timeSinceLastHeartbeat + " Constants.TIMEOUT_NANOS = " + Constants.TIMEOUT_NANOS);
                markMemberFailed(set.getKey());
                heartbeatReceiveTimes.remove(set.getKey());
                if (membershipTable.getLeaderId() == -1) {
                    logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Leader Failed. Election will happen.");
                    ElectionModule electionModule = ElectionModule.getElectionModule(thisBrokerInfo, loadBalancerIp, loadBalancerPort);
                    if (!electionModule.getElectionStatus()) {
                        electionModule.setElectionStatus(true);
                        electionModule.startElection();
                    }
                }
            }
        }

        if (membershipTable.getFailedMembersIdList().size() > 0) {
            sendFailedMembersListToLB();
        }
    }

    /**
     *
     */
    public void sendFailedMembersListToLB() {
        List<Integer> failedMembersIdList = membershipTable.getFailedMembersIdList();
        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] failedMemberList: " + failedMembersIdList);
        if (thisBrokerInfo.getBrokerId() == membershipTable.getLeaderId() && !failedMembersIdList.isEmpty()) {
            Connection loadBalancerConnection = null;
            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Establishing new connection with loadBalancer.");
            try {
                loadBalancerConnection = Utility.establishConnection(loadBalancerIp, loadBalancerPort);
            } catch (ConnectionClosedException e) {
                logger.info(e.getMessage());
            }
            if (loadBalancerConnection != null && loadBalancerConnection.connectionIsOpen()) {
                //send the list of failed broker Info.
                Any failedMemberInfo = Any.pack(FailedMemberInfo.FailedMemberInfoDetails.newBuilder()
                        .setRequestSenderType(Constants.BROKER)
                        .addAllFailedBrokerId(failedMembersIdList)
                        .build());
                boolean memberStatusUpdated = false;
                while(!memberStatusUpdated) {
                    try {
                        logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Sending failedMemberList. " + failedMembersIdList);
                        loadBalancerConnection.send(failedMemberInfo.toByteArray());
                        byte[] receivedUpdateResponse = loadBalancerConnection.receive();
                        if (receivedUpdateResponse != null) {
                            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] received Response. of failedMemberUpdate from loadBalancer.");
                            memberStatusUpdated = true;
                        }
                    } catch (ConnectionClosedException e) {
                        logger.info(e.getMessage());
                        loadBalancerConnection.closeConnection();
                    }
                }
                membershipTable.resetFailedMembersList();
                loadBalancerConnection.closeConnection();
            }
        }
    }

    /**
     * sends heartbeat message to all the active member in the membership table.
     */
    public void heartbeatSend() {
        for (Map.Entry<Integer, BrokerInfo> set : membershipTable.getMembershipInfo().entrySet()) {
            Any any = Any.pack(HeartBeatMessage.HeartBeatMessageDetails.newBuilder()
                    .setHeartBeat(true)
                    .build());
            logger.info("\n[ThreadId: " + Thread.currentThread().getId() + "] Sending HB Message to broker with Id : " + set.getKey());
            set.getValue().sendOverHeartbeatConnection(any.toByteArray());
        }
    }

    /**
     *
     * @param brokerId
     */
    public void markMemberFailed(int brokerId) {
        membershipTable.markMemberFailed(brokerId);
    }

    /**
     *
     * @param memberId
     */
    public void updateHeartBeat(int memberId) {
        heartbeatReceiveTimes.put(memberId, System.nanoTime());
    }
}
