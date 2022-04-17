package util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import connection.Connection;
import model.BrokerConfig;
import model.ConfigInformation;
import model.LoadBalancerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
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
        return type.equals(Constants.PRODUCER) || type.equals(Constants.CONSUMER) || type.equals(Constants.BROKER) || type.equals(Constants.LOAD_BALANCER);
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
     * reads configFile and returns model.ConfigInformation class obj
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
            logger.error("\nIOException occurred while initialising BufferedReader on file " + inputFileName + ". Error Message : " + e.getMessage());
        }
        return bufferedReader;
    }

    /**
     *
     * @param memberIP
     * @param memberPort
     * @return
     */
    public static Connection establishConnection(String memberIP, int memberPort) {
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
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        return connection;
    }
}