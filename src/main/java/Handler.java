import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import customeException.ConnectionClosedException;
import model.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.*;

import java.util.ArrayList;

/**
 * class that handles connection with any node of the system with loadBalancer at loadBalancer.
 * @author nilimajha
 */
public class Handler implements Runnable {
    private static final Logger logger = LogManager.getLogger(Handler.class);
    private Connection connection;
    private String connectionWith;
    private String loadBalancerName;
    private LoadBalancerDataStore loadBalancerDataStore;

    /**
     * Constructor
     * @param connection
     * @param loadBalancerName
     */
    public Handler(Connection connection, String loadBalancerName, LoadBalancerDataStore loadBalancerDataStore) {
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
                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + " Received request from " + connectionWith + " of type RequestLeaderAndMembersInfo.");
                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + " Sending Leader's info with Membership table info in Response...");
                            connection.send(getResponseLeaderInfoMessage(requestMessage));
                        } else if (any.is(UpdateLeaderInfo.UpdateLeaderInfoDetails.class)) {
                            UpdateLeaderInfo.UpdateLeaderInfoDetails updateRequestMessage =
                                    any.unpack(UpdateLeaderInfo.UpdateLeaderInfoDetails.class);
                            connectionWith = updateRequestMessage.getRequestSenderType();
                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + " Received request from " + connectionWith + " of type UpdateLeaderInfo.");
                            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + " Sending Update successful Response...");
                            connection.send(getLeaderUpdatedResponseMessage(updateRequestMessage));
                            logger.info("[ThreadId : " + Thread.currentThread().getId() + " Current Leader : " + loadBalancerDataStore.getLeaderInfo().getBrokerName());
                        }
                    } catch (InvalidProtocolBufferException e) {
                        logger.error("\nInvalidProtocolBufferException occurred decoding message received at loadBalancer. Error Message : "
                                + e.getMessage());
                    }
                }
            } catch (ConnectionClosedException e) {
                logger.info(e.getMessage());
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
            logger.info("\nleader is not null");
            isAvailable = true;
            if (connectionWith.equals(Constants.BROKER) && requestMessage.getAssignBrokerId()) {
                ArrayList<ByteString> membersInfoBytesList = getMembersBytesList();
                int memberId = loadBalancerDataStore.getId();
                any = Any.pack(ResponseLeaderInfo.ResponseLeaderAndMembersInfoDetails.newBuilder()
                        .setMessageId(requestMessage.getMessageId())
                        .setInfoAvailable(isAvailable)
                        .setLeaderName(currentLeaderInfo.getBrokerName())
                        .setLeaderIP(currentLeaderInfo.getBrokerIP())
                        .setLeaderPort(currentLeaderInfo.getBrokerPort())
                        .setBrokerId(memberId)
                        .addAllMembers(membersInfoBytesList)
                        .build());
                logger.info("\nAdding new broker into the memberShipList.");
                // adding this new member in the membership table.
                loadBalancerDataStore.addNewMemberIntoMembershipTable(memberId, requestMessage.getRequestSenderName(),
                        requestMessage.getBrokerIP(), requestMessage.getBrokerPort());
                logger.info("\nAdded Id : " + memberId + " name : " + requestMessage.getRequestSenderName() +
                        " IP : " + requestMessage.getBrokerIP() + " port : " + requestMessage.getBrokerPort());
            } else {
                any = Any.pack(ResponseLeaderInfo.ResponseLeaderAndMembersInfoDetails.newBuilder()
                        .setMessageId(requestMessage.getMessageId())
                        .setInfoAvailable(isAvailable)
                        .setLeaderName(currentLeaderInfo.getBrokerName())
                        .setLeaderIP(currentLeaderInfo.getBrokerIP())
                        .setLeaderPort(currentLeaderInfo.getBrokerPort())
                        .build());
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + " leader is available.");
            }
        } else {
            logger.info("\n[ThreadId : " + Thread.currentThread().getId() + " leader is null");
            if (connectionWith.equals(Constants.BROKER) && requestMessage.getAssignBrokerId()) {
                ArrayList<ByteString> membersInfoBytesList = getMembersBytesList();
                int memberId = loadBalancerDataStore.getId();
                any = Any.pack(ResponseLeaderInfo.ResponseLeaderAndMembersInfoDetails.newBuilder()
                        .setMessageId(requestMessage.getMessageId())
                        .setInfoAvailable(isAvailable)
                        .setBrokerId(memberId)
                        .addAllMembers(membersInfoBytesList)
                        .build());
                logger.info("\n[ThreadId : " + Thread.currentThread().getId() + " Adding new broker into the memberShipList. 1st broker.");
                // adding this new member in the membership table.
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
     * method creates MemberInfo protobuff of each BrokerInfo obj in membership table
     * and converts them into byte array and returns the list of it.
     * @return membersInfoBytesList
     */
    public ArrayList<ByteString> getMembersBytesList() {
        ArrayList<ByteString> membersInfoBytesList = new ArrayList<>();
        ArrayList<BrokerInfo> membersInfoList = loadBalancerDataStore.getMembershipInfo();
        for (BrokerInfo eachMember : membersInfoList) {
            logger.info("\nEach memberId : " + eachMember.getBrokerName() +
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
     *
     * @param updateRequestMessage
     * @return
     */
    public byte[] getLeaderUpdatedResponseMessage (UpdateLeaderInfo.UpdateLeaderInfoDetails updateRequestMessage) {
        boolean updateSuccessful = false;
        if (updateRequestMessage.getRequestSenderType().equals(Constants.BROKER)) {
            logger.info("\nUpdating leader info. new Leader Id : " + updateRequestMessage.getBrokerId());
            updateSuccessful = loadBalancerDataStore.updateLeaderInfo(updateRequestMessage.getBrokerId());
        }
        Any any = Any.pack(LeaderUpdatedResponse.LeaderUpdatedResponseDetails.newBuilder()
                .setMessageId(updateRequestMessage.getMessageId())
                .setUpdateSuccessful(updateSuccessful)
                .build());
        return  any.toByteArray();
    }

    /**
     *
     */
    @Override
    public void run() {
        start();
    }
}
