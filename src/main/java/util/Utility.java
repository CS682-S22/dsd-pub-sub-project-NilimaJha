package util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import connection.Connection;
import customeException.ConnectionClosedException;
import model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import proto.StartSyncUpMessage;
import proto.UpdateLeaderInfo;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * util.Utility class to store the helper functions.
 * @author nilimajha
 */
public class Utility {
    private static final Logger logger = LogManager.getLogger(Utility.class);

    /**
     * method check the validity of the argument provided.
     * @param args argument
     * @return true/false
     */
    public static boolean argsIsValid (String[] args) {
        boolean isValid = false;
        if (args.length == 6 && args[0].equals("-type") && args[2].equals("-name")
                && args[4].equals("-configFile") && typeIsValid(args[1]) && fileNameIsValid(args[5])) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * check the validity of the type provided in the argument.
     * @param type name provided in the argument
     * @return true/false
     */
    public static boolean typeIsValid (String type) {
        return type.equals(Constants.PRODUCER) || type.equals(Constants.CONSUMER)
                || type.equals(Constants.BROKER) || type.equals(Constants.LOAD_BALANCER);
    }

    /**
     * check the validity of the all the config file provided.
     * @param fileName list of files
     * @return true/false
     */
    public static boolean fileNameIsValid(String fileName) {
        boolean valid = true;
        if (getFileExtension(fileName) == null || !getFileExtension(fileName).equals(".json")) {
            valid = false;
        }
        return valid;
    }

    /**
     * extracts the extension of the given fileName that is in String format.
     * @param fileName file name
     * @return extension
     */
    public static String getFileExtension(String fileName) {
        String extension = null;
        int index = fileName.lastIndexOf(".");
        if (index > 0 && index < fileName.length() - 1) {
            extension = fileName.substring(index);
        }
        return extension;
    }

    /**
     * returns the type provided in the argument.
     * @param args argument
     * @return type
     */
    public static String getTypeFromArgs (String[] args) {
        return args[1];
    }

    /**
     * returns the name provided in the argument.
     * @param args arguments
     * @return name
     */
    public static String getNameFromArgs (String[] args) {
        return args[3];
    }

    /**
     * returns config file name provided in the argument.
     * @param args argument
     * @return filename
     */
    public static String getConfigFilename (String[] args) {
        return args[5];
    }

    /**
     * reads configFile and returns ConfigInformation class obj
     * which contains all the information of the producer or consumer
     * whose name is provided.
     * @param fileName config file name
     * @param name name provided in the args
     * @return configInformation
     */
    public static ConfigInformation extractConsumerOrPublisherConfigInfo(String fileName, String name) {
        List<ConfigInformation> hostDetailsList = null;
        ConfigInformation pubOrSubInfo = null;
        try {
            Reader configReader = Files.newBufferedReader(Paths.get(fileName));
            hostDetailsList = new Gson().fromJson(configReader, new TypeToken<List<ConfigInformation>>() {}.getType());
            configReader.close();

            for (ConfigInformation eachHostInfo : hostDetailsList) {
                if (eachHostInfo.getName().equals(name)) {
                    pubOrSubInfo = eachHostInfo;
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error("\nException occurred. Error Message :" + ex.getMessage());
        }
        return pubOrSubInfo;
    }

    /**
     * reads configFile and returns BrokerInformation class obj
     * which contains all the information of the producer or consumer
     * whose name is provided.
     * @param fileName config file name
     * @param name name provided in the args
     * @return BrokerConfig
     */
    public static LoadBalancerConfig extractLoadBalancerInfo(String fileName, String name) {
        List<LoadBalancerConfig> loadBalancerDetails = null;
        LoadBalancerConfig loadBalancerInfo = null;
        try {
            Reader configReader = Files.newBufferedReader(Paths.get(fileName));
            loadBalancerDetails = new Gson().fromJson(configReader, new TypeToken<List<LoadBalancerConfig>>() {}.getType());
            configReader.close();

            for (LoadBalancerConfig eachHostInfo : loadBalancerDetails) {
                if (eachHostInfo.getName().equals(name)) {
                    loadBalancerInfo = eachHostInfo;
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error("\nException Occurred. Error Message : " + ex.getMessage());
        }
        return loadBalancerInfo;
    }

    /**
     * reads configFile and returns BrokerInformation class obj
     * which contains all the information of the producer or consumer
     * whose name is provided.
     * @param fileName config file name
     * @param name name provided in the args
     * @return BrokerConfig
     */
    public static BrokerConfig extractBrokerConfigInfo(String fileName, String name) {
        List<BrokerConfig> brokerDetails = null;
        BrokerConfig brokerInfo = null;
        try {
            Reader configReader = Files.newBufferedReader(Paths.get(fileName));
            brokerDetails = new Gson().fromJson(configReader, new TypeToken<List<BrokerConfig>>() {}.getType());
            configReader.close();

            for (BrokerConfig eachHostInfo : brokerDetails) {
                if (eachHostInfo.getName().equals(name)) {
                    brokerInfo = eachHostInfo;
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return brokerInfo;
    }

    /**
     * initialises the FileInputStream named fileWriter of the class and deletes the file if already exist.
     * @param outputFileName file on which writting is to be performed
     */
    public static FileOutputStream fileWriterInitializer (String outputFileName) {
        File outputFile = new File(outputFileName);
        FileOutputStream fileWriter = null;
        if(outputFile.exists()){
            outputFile.delete();
        }  //deleting file if exist
        try {
            fileWriter = new FileOutputStream(outputFileName, true);
        } catch (FileNotFoundException e) {
            logger.error("\nFileNotFoundException occurred while Initialising FileOutPutStream for file "
                    + outputFileName + ". Error Message : " + e.getMessage());
        }
        return fileWriter;
    }

    /**
     * initialises the FileInputStream named fileReader of the class.
     * @param inputFileName file from where read is to be performed
     * @return fileReader
     */
    public static BufferedReader fileReaderInitializer (String inputFileName) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(inputFileName));
        } catch (IOException e) {
            logger.error("\nIOException occurred while initialising BufferedReader on file " + inputFileName +
                    ". Error Message : " + e.getMessage());
        }
        return bufferedReader;
    }

    /**
     * tries to establish connection with the host running on the given IP and Port.
     * @param memberIP
     * @param memberPort
     * @return Connection or null
     */
    public static Connection establishConnection(String memberIP, int memberPort) throws ConnectionClosedException {
        AsynchronousSocketChannel clientSocket = null;
        Connection connection = null;
        try {
            clientSocket = AsynchronousSocketChannel.open();
            InetSocketAddress brokerAddress = new InetSocketAddress(memberIP, memberPort);
            logger.info("\n[Connecting To Member] BrokerIP : "
                    + memberIP + " BrokerPort : " + memberPort);
            Future<Void> futureSocket = clientSocket.connect(brokerAddress);
            futureSocket.get();
            logger.info("\n[Connected to Member.]");
            connection = new Connection(clientSocket); //connection established with this member.
        } catch (IOException | ExecutionException | InterruptedException e) {
            logger.error(e.getMessage());
            throw new ConnectionClosedException("No Host running on the given IP & port!!!");
        }
        return connection;
    }

    /**
     * creates the protobuff message of one of the following type on the basis of the typeOfMessage attribute provided.
     * DBSnapshot.DBSnapshotDetails or StartSyncUpMessage.StartSyncUpMessageDetails
     * @param dbSnapshot
     * @return byte[]
     */
    public static byte[] getDBSnapshotMessageBytes(DBSnapshot dbSnapshot, int brokerId, String typeOfMessage) {
        List<ByteString> eachTopicSnapshotList = new ArrayList<>();
        for (Map.Entry<String, TopicSnapshot> eachTopicSnapshot : dbSnapshot.getTopicSnapshotMap().entrySet()) {
            Any topicSnapshot = Any.pack(proto.TopicSnapshot.TopicSnapshotDetails.newBuilder()
                    .setTopic(eachTopicSnapshot.getKey())
                    .setOffset(eachTopicSnapshot.getValue().getOffset())
                    .build());
            eachTopicSnapshotList.add(ByteString.copyFrom(topicSnapshot.toByteArray()));
        }
        if (!typeOfMessage.equals(Constants.START_SYNC)) {
            Any dbSnapshotAny = Any.pack(proto.DBSnapshot.DBSnapshotDetails.newBuilder()
                    .setMemberId(brokerId)
                    .addAllTopicSnapshot(eachTopicSnapshotList)
                    .build());
            return dbSnapshotAny.toByteArray();
        } else {
            Any startSyncMessage = Any.pack(StartSyncUpMessage.StartSyncUpMessageDetails.newBuilder()
                    .setMemberId(brokerId)
                    .addAllTopicSnapshot(eachTopicSnapshotList)
                    .build());
            return  startSyncMessage.toByteArray();
        }
    }

    /**
     * sends the updateLeaderInfo message to the loadBalancer over the connection provided.
     */
    public static boolean sendUpdateLeaderMessageToLB(Connection loadBalancerConnection, String brokerName, int brokerId) {
        int messageId = 0;
        Any updateLeaderMessage = Any.pack(UpdateLeaderInfo.UpdateLeaderInfoDetails.newBuilder()
                .setMessageId(messageId)
                .setRequestSenderType(Constants.BROKER)
                .setBrokerName(brokerName)
                .setBrokerId(brokerId)
                .build());
        boolean leaderUpdated = false;
        logger.info("\n[ThreadId : " + Thread.currentThread().getId() + "] Sending Update Leader info Message to Load Balancer.");
        while (!leaderUpdated) {
            try {
                loadBalancerConnection.send(updateLeaderMessage.toByteArray());
                byte[] receivedUpdateResponse = loadBalancerConnection.receive();
                if (receivedUpdateResponse != null) {
                    leaderUpdated = true;
                }
            } catch (ConnectionClosedException e) {
                logger.info(e.getMessage());
                loadBalancerConnection.closeConnection();
            }
        }
        return leaderUpdated;
    }
}