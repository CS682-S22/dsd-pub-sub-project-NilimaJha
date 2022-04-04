import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.BrokerConfig;
import model.ConfigInformation;
import model.Constants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Utility class to store the helper functions.
 * @author nilimajha
 */
public class Utility {

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
        return type.equals(Constants.PRODUCER) || type.equals(Constants.CONSUMER) || type.equals(Constants.BROKER);
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
            ex.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return bufferedReader;
    }
}