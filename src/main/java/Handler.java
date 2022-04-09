import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import model.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.*;

public class Handler implements Runnable {
    private static final Logger logger = LogManager.getLogger(RequestProcessor.class);
    private Connection connection;
    private String connectionWith;
    private LeaderInfo leaderInfo;
    private boolean requestServed;

    /**
     * Constructor
     * @param connection
     * @param connectionWith
     */
    public Handler(Connection connection, String connectionWith, LeaderInfo leaderInfo) {
        this.connection = connection;
        this.connectionWith = connectionWith;
        this.leaderInfo = leaderInfo;
    }

    /**
     *
     */
    public void start() {
        while (!requestServed) {
            byte[] receivedRequest = connection.receive();
            if (receivedRequest != null) {
                try {
                    Any any = Any.parseFrom(receivedRequest);
                    if (any.is(RequestLeaderInfo.RequestLeaderInfoDetails.class)) {
                        RequestLeaderInfo.RequestLeaderInfoDetails requestMessage =
                                any.unpack(RequestLeaderInfo.RequestLeaderInfoDetails.class);
                        connection.send(getResponseLeaderInfoMessage(requestMessage));
                    } else if (any.is(UpdateLeaderInfo.UpdateLeaderInfoDetails.class)) {
                        UpdateLeaderInfo.UpdateLeaderInfoDetails updateRequestMessage =
                                any.unpack(UpdateLeaderInfo.UpdateLeaderInfoDetails.class);
                        connection.send();
                    }
                } catch (InvalidProtocolBufferException e) {
                    logger.error("\nInvalidProtocolBufferException occurred decoding message received at loadBalancer. Error Message : "
                            + e.getMessage());
                }
            }
        }
    }

    /**
     * forms responseLeaderInfo message wrap it into Any and returns its byte array.
     * @param requestMessage
     * @return any.toByteArray()
     */
    public byte[] getResponseLeaderInfoMessage (RequestLeaderInfo.RequestLeaderInfoDetails requestMessage) {
        LeaderInfo currentLeaderInfo = leaderInfo.getLeaderInfo();
        Any any = Any.pack(ResponseLeaderInfo.ResponseLeaderInfoDetails.newBuilder()
                .setMessageId(requestMessage.getMessageId())
                .setLeaderName(currentLeaderInfo.getLeaderName())
                .setLeaderIP(currentLeaderInfo.getLeaderIP())
                .setLeaderPort(currentLeaderInfo.getLeaderPort())
                .build());
        return any.toByteArray();
    }

    /**
     *
     * @param updateRequestMessage
     * @return
     */
    public byte[] getLeaderUpdatedResponseMessage (UpdateLeaderInfo.UpdateLeaderInfoDetails updateRequestMessage) {
        if (updateRequestMessage.getSenderType().equals(Constants.BROKER)) {
            leaderInfo.updateLeaderInfo(updateRequestMessage.getBrokerName(),
                    updateRequestMessage.getBrokerId(),
                    updateRequestMessage.getBrokerIP(),
                    updateRequestMessage.getBrokerPort());
        }
        Any any = Any.pack(LeaderUpdatedResponse.LeaderUpdatedResponseDetails.newBuilder()
                .setMessageId(messageId)
                .setUpdateSuccessful(updateSuccessful)
                .build());
        return  any.toByteArray();
    }

    @Override
    public void run() {

    }
}
