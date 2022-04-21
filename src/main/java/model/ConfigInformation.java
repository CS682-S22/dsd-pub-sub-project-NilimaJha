package model;

import java.util.ArrayList;

/**
 * Class to keep information of producer and consumer from config file.
 * @author nilimajha
 */
public class ConfigInformation {
    private String Name;
    private String Type;
    private ArrayList<String> ConnectTo;
    private String FileName;
    private String TopicName;
    private String BrokerIP;
    private int BrokerPort;
    private String LoadBalancerName;
    private String LoadBalancerIP;
    private int LoadBalancerPort;

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
    public ConfigInformation(String Name, String Type, ArrayList<String> ConnectTo, String FileName,
                             String TopicName, String BrokerIP, int BrokerPort) {
        this.Name = Name;
        this.Type = Type;
        this.ConnectTo = ConnectTo;
        this.FileName = FileName;
        this.TopicName = TopicName;
        this.BrokerIP = BrokerIP;
        this.BrokerPort = BrokerPort;
    }

    /**
     * Constructor
     * @param Name
     * @param Type
     * @param ConnectTo
     * @param FileName
     * @param TopicName
     * @param LoadBalancerName
     * @param LoadBalancerIP
     * @param LoadBalancerPort
     */
    public ConfigInformation(String Name, String Type, ArrayList<String> ConnectTo, String FileName,
                             String TopicName, String LoadBalancerName, String LoadBalancerIP, int LoadBalancerPort) {
        this.Name = Name;
        this.Type = Type;
        this.ConnectTo = ConnectTo;
        this.FileName = FileName;
        this.TopicName = TopicName;
        this.LoadBalancerName = LoadBalancerName;
        this.LoadBalancerIP = LoadBalancerIP;
        this.LoadBalancerPort= LoadBalancerPort;
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

    /**
     * getter for LoadBalancerIP
     * @return LoadBalancerIP
     */
    public String getLoadBalancerIP() {
        return LoadBalancerIP;
    }

    /**
     * getter for LoadBalancerPort
     * @return LoadBalancerPort
     */
    public int getLoadBalancerPort() {
        return LoadBalancerPort;
    }

    /**
     * getter for LoadBalancerName
     * @return LoadBalancerName
     */
    public String getLoadBalancerName() {
        return LoadBalancerName;
    }
}
