import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.BrokerConfig;
import model.ConfigInformation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 */
public class Utility {

    /**
     * method check the validity of the argument provided.
     * @param args
     * @return
     */
    public static boolean argsIsValid (String[] args) {
        boolean isValid = false;
        if (args.length == 4) {
            if (args[0].equals("-name") && args[2].equals("-configFile")) {
                if (nameIsValid(args[1]) && fileNameIsValid(args[3])) {
                    isValid = true;
                }
            }
        }
        return isValid;
    }

    /**
     *
     * @param name
     * @return
     */
    public static boolean nameIsValid (String name) {
        boolean nameIsValid = false;
        String[] namePart = name.split("-");
        if (namePart[0].equals("PRODUCER") || namePart[0].equals("CONSUMER") || namePart[0].equals("BROKER")) {
            nameIsValid = true;
        }
        return nameIsValid;
    }

    /**
     * method fileIsValid()
     * check the validity of the all the files in the file list.
     * @param fileName list of files
     * @return
     */
    public static boolean fileNameIsValid(String fileName) {
        boolean valid = true;
        if (!getFileExtension(fileName).equals(".json")) {
            valid = false;
            System.out.println("file not valid...");
        }
        return valid;
    }

    /**
     * method getFileExtension()
     * extracts the extension of the given fileName that is in String format.
     * @param fileName
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
     *
     * @param args
     * @return
     */
    public static String getNameFromArgs (String[] args) {
        return args[1];
    }

    /**
     *
     * @param args
     * @return
     */
    public static String getConfigFilename (String[] args) {
        return args[3];
    }

    /**
     * reads configFile and returns model.ConfigInformation class obj
     * which contains all the information of the producer or consumer
     * whose name is provided.
     * @param fileName
     * @return
     */
    public static ConfigInformation extractConsumerOrPublisherConfigInfo(String fileName, String name) {
        List<ConfigInformation> hostDetailsList = null;
        try {
            Reader configReader = Files.newBufferedReader(Paths.get(fileName));
            hostDetailsList = new Gson().fromJson(configReader, new TypeToken<List<ConfigInformation>>() {}.getType());
            configReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ConfigInformation pubOrSubInfo = null;
        for (ConfigInformation eachHostInfo : hostDetailsList) {
            if (eachHostInfo.getName().equals(name)) {
                pubOrSubInfo = eachHostInfo;
            }
        }
        return pubOrSubInfo;
    }

    /**
     * reads configFile and returns BrokerInformation class obj
     * which contains all the information of the producer or consumer
     * whose name is provided.
     * @param fileName
     * @return
     */
    public static BrokerConfig extractBrokerConfigInfo(String fileName, String name) {
        List<BrokerConfig> brokerDetails = null;
        try {
            Reader configReader = Files.newBufferedReader(Paths.get(fileName));
            brokerDetails = new Gson().fromJson(configReader, new TypeToken<List<BrokerConfig>>() {}.getType());
            configReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        BrokerConfig brokerInfo = null;
        for (BrokerConfig eachHostInfo : brokerDetails) {
            if (eachHostInfo.getName().equals(name)) {
                brokerInfo = eachHostInfo;
            }
        }
        return brokerInfo;
    }

    /**
     * initialises the FileInputStream named fileWriter of the class and deletes the file if already exist.
     * @param outputFileName
     */
    public static FileOutputStream fileWriterInitializer (String outputFileName) {
        System.out.printf("\n[Inside FileWriterInitializer] [outputFileName : %s] \n", outputFileName);
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
     * initialises the FileInputStream named fileWriter of the class and deletes the file if already exist.
     * @param outputFileName
     */
    public static BufferedWriter fileWriterInitializer2 (String outputFileName) {
        System.out.printf("\n[Inside FileWriterInitializer] [outputFileName : %s] \n", outputFileName);
        File outputFile = new File(outputFileName);
        BufferedWriter bufferedWriter = null;
        if(outputFile.exists()){
            outputFile.delete();
        }  //deleting file if exist
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(outputFileName, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferedWriter;
    }

    /**
     * initialises the FileInputStream named fileReader of the class.
     * @param inputFileName
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
