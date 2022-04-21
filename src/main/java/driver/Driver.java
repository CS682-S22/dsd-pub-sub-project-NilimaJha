package driver;

import broker.Broker;
import consumer.Consumer;
import loadBalancer.LoadBalancer;
import model.BrokerConfig;
import model.ConfigInformation;
import producer.Producer;
import util.Constants;
import model.LoadBalancerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.Utility;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Scanner;

/**
 * Driver class that contains main method.
 * @author nilimajha
 */
public class Driver {
    private static final Logger logger = LogManager.getLogger(Driver.class);

    /**
     * main method
     * @param args input args
     */
    public static void main (String[] args) {
        //validate args
        if (!Utility.argsIsValid(args)) {
            logger.info("\nArgument provided is invalid.");
            System.exit(0);
        }
        //parseArgs
        String hostType = Utility.getTypeFromArgs(args);
        String hostName = Utility.getNameFromArgs(args);
        String configFileName = Utility.getConfigFilename(args);

        switch (hostType) {
            case Constants.LOAD_BALANCER:
                createAndStartLoadBalancer(configFileName, hostName);
                break;
            case Constants.BROKER:
                createAndStartBroker(configFileName, hostName);
                break;
            case Constants.PRODUCER:
                createAndStartProducer(configFileName, hostName);
                break;
            default:  // consumer
                createAndStartConsumer(configFileName, hostName);
                break;
        }
    }

    /**
     * method Creates Broker object and starts it
     * @param configFileName broker Config Filename
     * @param brokerName broker name
     */
    public static void createAndStartBroker (String configFileName, String brokerName) {
        BrokerConfig brokerConfig = Utility.extractBrokerConfigInfo(configFileName, brokerName);
        Broker broker = new Broker(
                brokerConfig.getName(),
                brokerConfig.getBrokerIP(),
                brokerConfig.getBrokerPort(),
                brokerConfig.getLoadBalancerName(),
                brokerConfig.getLoadBalancerIP(),
                brokerConfig.getLoadBalancerPort());

        logger.info("\nbroker.Broker name : " + broker.getName() + "broker.Broker IP : " );

        Thread thread = new Thread(broker);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            logger.error("\nInterruptedException occurred while waiting for broker thread to join. Error Message : " + e.getMessage());
        }
    }

    /**
     * method Creates loadBalancer object and starts it
     * @param configFileName loadBalancerConfig Filename
     * @param loadBalancerName loadBalancer name
     */
    public static void createAndStartLoadBalancer (String configFileName, String loadBalancerName) {
        System.out.println("LOAD-BALANCER");
        LoadBalancerConfig loadBalancerConfig = Utility.extractLoadBalancerInfo(configFileName, loadBalancerName);
        LoadBalancer loadBalancer = new LoadBalancer(
                loadBalancerConfig.getName(),
                loadBalancerConfig.getLoadBalancerIP(),
                loadBalancerConfig.getLoadBalancerPort());
        System.out.println("Starting Load Balancer.....");
        loadBalancer.startLoadBalancer();
    }

    /**
     * method Creates Producer object and starts it
     * @param configFileName producerConfigFileName
     * @param producerName Name of Producer
     */
    public static void createAndStartProducer (String configFileName, String producerName) {
        ConfigInformation configInformation = Utility.extractConsumerOrPublisherConfigInfo(configFileName, producerName);
        Producer producer = new Producer(
                configInformation.getName(),
                configInformation.getLoadBalancerName(),
                configInformation.getLoadBalancerIP(),
                configInformation.getLoadBalancerPort());

        if (producer.connectedToBroker()) {
            logger.info("\n[Now Sending Actual data]");
            try (BufferedReader bufferedReader = Utility.fileReaderInitializer(configInformation.getFileName())) {
                String eachLine = bufferedReader.readLine();
                String topic = null;
                int lineNo = 1;
                Scanner sc = new Scanner(System.in);
                System.out.println("\nEnter Command: ");
                String cmd = sc.nextLine();
                while (!cmd.equals("exit")) {
                    // Running the code until the input command is exit.
                    if (cmd.length() == 0 && eachLine != null) {
                        if (configInformation.getFileName().equals("Apache.log")) {
                            String[] messagePart = eachLine.split("] \\[");
                            if (messagePart.length != 1) {
                                if (messagePart[1].equals("error")) {
                                    topic = "Apache_error";
                                } else {
                                    topic = "Apache_notice";
                                }
                            }
                        } else if (configInformation.getFileName().equals("Zookeeper.log")) {
                            topic = "Zookeeper";
                        } else if (configInformation.getFileName().equals("Hadoop.log")) {
                            topic = "Hadoop";
                        } else {
                            topic = "Mac";
                        }

                        logger.info("\n[Now Sending Actual data] [Topic : " + topic + "]");
                        producer.send(topic, eachLine.getBytes());    // send data

                        if (configInformation.getFileName().equals("Apache.log")) {
                            String[] messagePart = eachLine.split("] \\[");
                            if (messagePart.length != 1) {
                                topic = "Apache";
                                logger.info("produced : " + producer.send(topic, eachLine.getBytes()));  // send data
                            }
                        }

                        logger.info("LineNumber : " + lineNo);
                        lineNo++;
                        eachLine = bufferedReader.readLine();   // reading next line from the file
                    }
                    System.out.println("\nEnter Command: ");
                    cmd = sc.nextLine();
                }
            } catch (IOException e) {
                logger.info("\nIOException occurred while initialising BufferedReader. Error Message : " + e.getMessage());
            }
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                logger.info("\nInterruptedException occurred with thread.sleep. Error Message : " + e.getMessage());
            }
        }
        producer.close();
    }

    /**
     * method Creates Consumer object and starts it
     * @param configFileName consumerConfigFileName
     * @param consumerName Name of consumer
     */
    public static void createAndStartConsumer (String configFileName, String consumerName) {
        ConfigInformation configInformation = Utility.extractConsumerOrPublisherConfigInfo(configFileName, consumerName);
        logger.info("\nConsumer Started\n");
        Consumer consumer = new Consumer(
                configInformation.getName(),
                configInformation.getType(),
                configInformation.getLoadBalancerName(),
                configInformation.getLoadBalancerIP(),
                configInformation.getLoadBalancerPort(),
                configInformation.getTopicName(),
                0);
        try (FileOutputStream fileWriter = Utility.fileWriterInitializer(configInformation.getFileName())) {
            // Continue to pull messages...forever
            while(!consumer.isShutdown()) {
                byte[] message = consumer.poll(Duration.ofMillis(100));
                // writing data on file.
                if (message != null) {
                    fileWriter.write(message);
                    fileWriter.write((byte)'\n');
                }
            }
        } catch (IOException e) {
            logger.info("\nIOException occurred while initialising fileWriter. Error Message : " + e.getMessage());
        }
    }
}
