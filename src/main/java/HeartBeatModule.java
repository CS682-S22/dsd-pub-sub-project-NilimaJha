import com.google.protobuf.Any;
import model.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.HeartBeatMessage;

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
    private MembershipTable membershipTable = MembershipTable.getMembershipTable(Constants.BROKER);
    private Timer heartbeatCheckTimer;
    private Timer heartbeatSendTimer;
    private static HeartBeatModule heartBeatModule = null;

    /**
     * Constructor
     */
    private HeartBeatModule() {
        startHeartbeatCheckTimer(); // starting heartbeatChecker timerTask.
        startHeartbeatSendTimer();  // start heartbeatSender timerTask.
    }

    /**
     *
     * @return
     */
    synchronized static HeartBeatModule getHeartBeatModule() {
        if (heartBeatModule == null) {
            heartBeatModule = new HeartBeatModule();
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
                logger.info("\nstarting the HB Check timer.");
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
                logger.info("\nstarting the HB Send timer.");
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
                logger.info("\n timeSinceLastHeartbeat = " + timeSinceLastHeartbeat + " Constants.TIMEOUT_NANOS = " + Constants.TIMEOUT_NANOS);
                markMemberFailed(set.getKey());
                heartbeatReceiveTimes.remove(set.getKey());
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
            logger.info("\nSending HB Message to broker with Id : " + set.getKey());
            set.getValue().sendHeartbeat(any.toByteArray());
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
