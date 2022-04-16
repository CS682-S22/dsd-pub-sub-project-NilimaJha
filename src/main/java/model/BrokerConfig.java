package model;

/**
 * Class to keep all the information from model.BrokerConfig file.
 * @author nilimajha
 */
public class BrokerConfig {
    private int BrokerId;
    private String Name;
    private String BrokerIP;
    private int BrokerPort;
    private String LoadBalancerName;
    private String LoadBalancerIP;
    private int LoadBalancerPort;

    /**
     * Constructor to initialise class attributes
     * @param Name
     * @param BrokerIP
     * @param BrokerPort
     */
    public BrokerConfig(String Name, String BrokerIP, int BrokerPort,
                        String loadBalancerName, String loadBalancerIP,
                        int loadBalancerPort) {
        this.Name = Name;
        this.BrokerIP = BrokerIP;
        this.BrokerPort = BrokerPort;
        this.LoadBalancerName = loadBalancerName;
        this.LoadBalancerIP = loadBalancerIP;
        this.LoadBalancerPort = loadBalancerPort;
    }

    /**
     * getter for attribute BrokerId
     * @return BrokerId
     */
    public int getBrokerId() {
        return BrokerId;
    }

    /**
     * getter for attribute Name
     * @return Name
     */
    public String getName() {
        return Name;
    }

    /**
     * getter for attribute BrokerIP
     * @return BrokerIP
     */
    public String getBrokerIP() {
        return BrokerIP;
    }

    /**
     * getter for attribute BrokerPort
     * @return BrokerPort
     */
    public int getBrokerPort() {
        return BrokerPort;
    }

    /**
     * etter for attribute loadBalancerName.
     * @return loadBalancerName
     */
    public String getLoadBalancerName() {
        return LoadBalancerName;
    }

    /**
     * getter for attribute loadBalancerIP.
     * @return loadBalancerIP
     */
    public String getLoadBalancerIP() {
        return LoadBalancerIP;
    }

    /**
     * getter for attribute loadBalancerPort.
     * @return loadBalancerPort
     */
    public int getLoadBalancerPort() {
        return LoadBalancerPort;
    }

    /**
     * setter for attribute brokerId
     * @param brokerId
     */
    public void setBrokerId(int brokerId) {
        BrokerId = brokerId;
    }
}
