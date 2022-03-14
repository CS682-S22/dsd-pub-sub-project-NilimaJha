/**
 * Class to keep all the information from BrokerConfig file.
 */
public class BrokerConfig {
    private String Name;
    private String BrokerIP;
    private int BrokerPort;

    /**
     * Constructor to initialise class attributes
     * @param Name
     * @param BrokerIP
     * @param BrokerPort
     */
    public BrokerConfig(String Name, String BrokerIP, int BrokerPort) {
        this.Name = Name;
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
