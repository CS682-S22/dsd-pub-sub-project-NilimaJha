package model;

import java.util.ArrayList;

/**
 * Class to keep information of producer and consumer from config file.
 */
public class ConfigInformation {
    private String Name;
    private String Type;
    private ArrayList<String> ConnectTo;
    private String FileName;
    private String TopicName;
    private String BrokerIP;
    private int BrokerPort;

    /**
     * Constructor
     * @param Name
     * @param BrokerIP
     * @param BrokerPort
     */
    public ConfigInformation(String Name, ArrayList<String> ConnectTo, String FileName, String BrokerIP, int BrokerPort) {
        this.Name = Name;
        this.ConnectTo = ConnectTo;
        this.FileName = FileName;
        this.BrokerIP = BrokerIP;
        this.BrokerPort = BrokerPort;
    }

    /**
     * Constructor
     * @param Name
     * @param BrokerIP
     * @param BrokerPort
     */
    public ConfigInformation(String Name, String Type, ArrayList<String> ConnectTo, String FileName, String TopicName, String BrokerIP, int BrokerPort) {
        this.Name = Name;
        this.Type = Type;
        this.ConnectTo = ConnectTo;
        this.FileName = FileName;
        this.TopicName = TopicName;
        this.BrokerIP = BrokerIP;
        this.BrokerPort = BrokerPort;
    }

    /**
     * getter for Name
     * @return Name
     */
    public String getName() {
        return Name;
    }

    /**
     * getter for Type
     * @return
     */
    public String getType() {
        return Type;
    }

    /**
     * getter for ConnectTo
     * @return ConnectTo
     */
    public ArrayList<String> getConnectTo() {
        return ConnectTo;
    }

    /**
     * getter for FileName
     * @return FileName
     */
    public String getFileName() {
        return FileName;
    }

    /**
     * getter for TopicName
     * @return TopicName
     */
    public String getTopicName() {
        return TopicName;
    }

    /**
     * getter for BrokerIP
     * @return BrokerIP
     */
    public String getBrokerIP() {
        return BrokerIP;
    }

    /**
     * getter for BrokerPort
     * @return BrokerPort
     */
    public int getBrokerPort() {
        return BrokerPort;
    }
}
