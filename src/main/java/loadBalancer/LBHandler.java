package loadBalancer;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import connection.Connection;
import customeException.ConnectionClosedException;
import model.BrokerInfo;
import model.LoadBalancerDataStore;
import util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.*;

import java.util.ArrayList;
import java.util.List;

/**
 * class that handles connection with any node of the system with loadBalancer at loadBalancer.
 * @author nilimajha
 */
public class LBHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(LBHandler.class);
    private Connection connection;
    private String connectionWith;
    private String loadBalancerName;
    private LoadBalancerDataStore loadBalancerDataStore;

    /**
     * Constructor
     * @param connection
     * @param loadBalancerName
     */
    public LBHandler(Connection connection, String loadBalancerName, LoadBalancerDataStore loadBalancerDataStore) {
        this.connection = connection;
        this.loadBalancerName = loadBalancerName;
        this.loadBalancerDataStore = loadBalancerDataStore;
    }

    /**
     * receives the request message from the other end and
     * sends back the appropriate response over the same connection
     * and closes the connection.
     */
    public void start() {
        while (connection.isConnected()) {
            try {
                byte[] receivedRequest = connection.receive();
                if (receivedRequest != null) {
                    try {
                        Any any = Any.parseFrom(receivedRequest);
                        if (any.is(RequestLeaderAndMembersInfo.RequestLeaderAndMembersInfoDetails.class)) {
                            RequestLeaderAndMembersInfo.RequestLeaderAndMembersInfoDetails requestMessage =
                                    any.unpack(RequestLeaderAndMembersInfo.RequestLeaderAndMembersInfoDetails.class);
                            connectionWith = requestMessage.getRequestSenderType();
                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Received request from " + connectionWith + " of type RequestLeaderAndMembersInfo.");
                            connection.send(getResponseLeaderInfoMessage(requestMessage));
                        } else if (any.is(UpdateLeaderInfo.UpdateLeaderInfoDetails.class)) {
                            UpdateLeaderInfo.UpdateLeaderInfoDetails updateRequestMessage =
                                    any.unpack(UpdateLeaderInfo.UpdateLeaderInfoDetails.class);
                            connectionWith = updateRequestMessage.getRequestSenderType();
                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Received request from " + connectionWith + " to update Leader info.");
                            connection.send(getLeaderUpdatedResponseMessage(updateRequestMessage));
                        } else if (any.is(FailedMemberInfo.FailedMemberInfoDetails.class)) {
                            FailedMemberInfo.FailedMemberInfoDetails failedMemberInfoDetails =
                                    any.unpack(FailedMemberInfo.FailedMemberInfoDetails.class);
                            connectionWith = failedMemberInfoDetails.getRequestSenderType();
                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Received request from " + connectionWith + " to update membership Information at load-balancer");
                            List<Integer> failedMembers = failedMemberInfoDetails.getFailedBrokerIdList();
                            if (connectionWith.equals(Constants.BROKER)) {
                                for (int failedMemberId : failedMembers) {
                                    loadBalancerDataStore.markMemberDown(failedMemberId);
                                }
                                connection.send(getMembershipTableUpdatedResponseMessage(failedMemberInfoDetails));
                                logger.info("[ThreadId : " + Thread.currentThread().getId() + "] Current Leader : " + loadBalancerDataStore.getLeaderInfo().getBrokerName());
                                logger.info("\nmembers size : " + loadBalancerDataStore.getMembershipInfo().size());
                            }
                        } else if (any.is(RequestBrokerInfo.RequestBrokerInfoDetails.class)) {
                            RequestBrokerInfo.RequestBrokerInfoDetails requestBrokerInfoDetails =
                                    any.unpack(RequestBrokerInfo.RequestBrokerInfoDetails.class);
                            connectionWith = requestBrokerInfoDetails.getRequestSenderType();
                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + " Received request from " + connectionWith + " of type RequestBrokerInfo.");
                            connection.send(getResponseRandomBrokerInfoMessage(requestBrokerInfoDetails));
                        }
                    } catch (InvalidProtocolBufferException e) {
                        logger.error("\n[ThreadId : " + Thread.currentThread().getId() + " InvalidProtocolBufferException occurred decoding message received at loadBalancer. Error Message : "
                                + e.getMessage());
                    }
                }
            } catch (ConnectionClosedException e) {
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Closing the connection");
                connection.closeConnection();
            }
        }
    }

    /**
     * forms responseLeaderInfo message wrap it into Any and returns its byte array.
     * @param requestMessage
     * @return any.toByteArray()
     */
    public byte[] getResponseLeaderInfoMessage (RequestLeaderAndMembersInfo.RequestLeaderAndMembersInfoDetails requestMessage) {
        BrokerInfo currentLeaderInfo = loadBalancerDataStore.getLeaderInfo();
        Any any;
        boolean isAvailable = false;
        if (currentLeaderInfo != null) {
            isAvailable = true;
            if (connectionWith.equals(Constants.BROKER) && requestMessage.getAssignBrokerId()) {
                ArrayList<ByteString> membersInfoBytesList = getMembersBytesList();
                int memberId = loadBalancerDataStore.getId();
                any = Any.pack(ResponseLeaderInfo.ResponseLeaderAndMembersInfoDetails.newBuilder()
                        .setMessageId(requestMessage.getMessageId())
                        .setInfoAvailable(isAvailable)
                        .setLeaderName(currentLeaderInfo.getBrokerName())
                        .setLeaderID(currentLeaderInfo.getBrokerId())
                        .setLeaderIP(currentLeaderInfo.getBrokerIP())
                        .setLeaderPort(currentLeaderInfo.getBrokerPort())
                        .setBrokerId(memberId)
                        .addAllMembers(membersInfoBytesList)
                        .build());
                loadBalancerDataStore.addNewMemberIntoMembershipTable(memberId, requestMessage.getRequestSenderName(),
                        requestMessage.getBrokerIP(), requestMessage.getBrokerPort());
            } else {
                any = Any.pack(ResponseLeaderInfo.ResponseLeaderAndMembersInfoDetails.newBuilder()
                        .setMessageId(requestMessage.getMessageId())
                        .setInfoAvailable(isAvailable)
                        .setLeaderName(currentLeaderInfo.getBrokerName())
                        .setLeaderIP(currentLeaderInfo.getBrokerIP())
                        .setLeaderPort(currentLeaderInfo.getBrokerPort())
                        .build());
//                logger.info("\n Leader Info : " + loadBalancerDataStore.getLeaderInfo().getBrokerId() + "/ " + currentLeaderInfo.getBrokerId() + " leaderName : " + currentLeaderInfo.getBrokerName());
            }
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Leader is available. Leader Info : " + loadBalancerDataStore.getLeaderInfo().getBrokerId());
        } else {
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Leader is null");
            if (connectionWith.equals(Constants.BROKER) && requestMessage.getAssignBrokerId()) {
                ArrayList<ByteString> membersInfoBytesList = getMembersBytesList();
                int memberId = loadBalancerDataStore.getId();
                any = Any.pack(ResponseLeaderInfo.ResponseLeaderAndMembersInfoDetails.newBuilder()
                        .setMessageId(requestMessage.getMessageId())
                        .setInfoAvailable(isAvailable)
                        .setBrokerId(memberId)
                        .addAllMembers(membersInfoBytesList)
                        .build());
                loadBalancerDataStore.addNewMemberIntoMembershipTable(memberId, requestMessage.getRequestSenderName(),
                        requestMessage.getBrokerIP(), requestMessage.getBrokerPort());
            } else {
                any = Any.pack(ResponseLeaderInfo.ResponseLeaderAndMembersInfoDetails.newBuilder()
                        .setMessageId(requestMessage.getMessageId())
                        .setInfoAvailable(isAvailable)
                        .build());
            }
        }
        return any.toByteArray();
    }

    /**
     * forms responseLeaderInfo message wrap it into Any and returns its byte array.
     * @param requestMessage
     * @return any.toByteArray()
     */
    public byte[] getResponseRandomBrokerInfoMessage (RequestBrokerInfo.RequestBrokerInfoDetails requestMessage) {
        BrokerInfo randomBrokerInfo = loadBalancerDataStore.getRandomFollowerBrokerInfo();
        Any any;
        if (randomBrokerInfo != null) {
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Broker Available for read.");
            any = Any.pack(ResponseRandomBrokerInfo.ResponseRandomBrokerInfoDetails.newBuilder()
                    .setInfoAvailable(true)
                    .setMessageId(requestMessage.getMessageId())
                    .setBrokerIP(randomBrokerInfo.getBrokerIP())
                    .setBrokerPort(randomBrokerInfo.getBrokerPort())
                    .setBrokerName(randomBrokerInfo.getBrokerName())
                    .build());
        } else {
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Broker is not Available for read.");
            any = Any.pack(ResponseRandomBrokerInfo.ResponseRandomBrokerInfoDetails.newBuilder()
                    .setInfoAvailable(false)
                    .setMessageId(requestMessage.getMessageId())
                    .build());
        }
        return any.toByteArray();
    }

    /**
     * method creates MemberInfo protobuff of each BrokerInfo obj in membership table
     * and converts them into byte array and returns the list of it.
     * @return membersInfoBytesList
     */
    public ArrayList<ByteString> getMembersBytesList() {
        ArrayList<ByteString> membersInfoBytesList = new ArrayList<>();
        ArrayList<BrokerInfo> membersInfoList = loadBalancerDataStore.getMembershipInfo();
        for (BrokerInfo eachMember : membersInfoList) {
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Each memberId : " + eachMember.getBrokerName() +
                    " Each memberName : " + eachMember.getBrokerName() +
                    " Each memberIP : " + eachMember.getBrokerIP() +
                    " Each memberPort : " + eachMember.getBrokerPort());

            MembersInfo.MembersInfoDetails membersInfoDetails = MembersInfo.MembersInfoDetails.newBuilder()
                    .setMemberId(eachMember.getBrokerId())
                    .setMemberName(eachMember.getBrokerName())
                    .setMemberIP(eachMember.getBrokerIP())
                    .setMemberPort(eachMember.getBrokerPort())
                    .build();
            membersInfoBytesList.add(ByteString.copyFrom(membersInfoDetails.toByteArray()));
        }
        return membersInfoBytesList;
    }

    /**
     * forms the LeaderUpdatedResponse to be sent to the Leader Member Broker.
     * @param updateRequestMessage
     * @return
     */
    public byte[] getLeaderUpdatedResponseMessage (UpdateLeaderInfo.UpdateLeaderInfoDetails updateRequestMessage) {
        boolean updateSuccessful = false;
        if (updateRequestMessage.getRequestSenderType().equals(Constants.BROKER)) {
            updateSuccessful = loadBalancerDataStore.updateLeaderInfo(updateRequestMessage.getBrokerId());
        }
        Any any = Any.pack(LeaderUpdatedResponse.LeaderUpdatedResponseDetails.newBuilder()
                .setMessageId(updateRequestMessage.getMessageId())
                .setUpdateSuccessful(updateSuccessful)
                .build());
        return  any.toByteArray();
    }

    /**
     * forms the FailedMemberInfo to be sent to the Leader Member Broker.
     * @param failedMemberInfoDetails
     * @return
     */
    public byte[] getMembershipTableUpdatedResponseMessage (FailedMemberInfo.FailedMemberInfoDetails failedMemberInfoDetails) {
        Any any = Any.pack(FailedMemberInfo.FailedMemberInfoDetails.newBuilder()
                .setRequestSenderType(failedMemberInfoDetails.getRequestSenderType())
                .build());
        return  any.toByteArray();
    }

    /**
     * run method calls start method.
     */
    @Override
    public void run() {
        start();
    }
}
