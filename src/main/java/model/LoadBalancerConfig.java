package model;

public class LoadBalancerConfig {
    private String Name;
    private String LoadBalancerIP;
    private int LoadBalancerPort;
    /**
     * Constructor to initialise class attributes
     * @param Name
     * @param BrokerIP
     * @param BrokerPort
     */
    public LoadBalancerConfig(String Name, String BrokerIP, int BrokerPort) {
        this.Name = Name;
        this.LoadBalancerIP = BrokerIP;
        this.LoadBalancerPort = BrokerPort;
    }

    /**
     * getter for Name
     * @return Name
     */
    public String getName() {
        return Name;
    }

    /**
     * getter for BrokerIP
     * @return BrokerIP
     */
    public String getLoadBalancerIP() {
        return LoadBalancerIP;
    }

    /**
     * getter for BrokerPort
     * @return BrokerPort
     */
    public int getLoadBalancerPort() {
        return LoadBalancerPort;
    }
}
