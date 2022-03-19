import model.BrokerConfig;
import model.ConfigInformation;
import model.Constants;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;

/**
 * Driver class that contains main method.
 * @author nilimajha
 */
public class Driver {

    /**
     * main method
     * @param args input args
     */
    public static void main (String[] args) {
        //validate args
        if (!Utility.argsIsValid(args)) {
            System.out.println("Argument provided is invalid.");
            System.exit(0);
        }
        //parseArgs
        String hostType = Utility.getTypeFromArgs(args);
        String hostName = Utility.getNameFromArgs(args);
        String configFileName = Utility.getConfigFilename(args);

        if (hostType.equals(Constants.BROKER)) {
            createAndStartBroker(configFileName, hostName);
        } else if (hostType.equals(Constants.PRODUCER)) {
            createAndStartProducer(configFileName, hostName);
        } else { //Consumer
            createAndStartConsumer(configFileName, hostName);
        }
    }

    /**
     * method Creates Broker object and starts it
     * @param configFileName broker Config Filename
     * @param brokerName broker name
     */
    public static void createAndStartBroker (String configFileName, String brokerName) {
        BrokerConfig brokerConfig = Utility.extractBrokerConfigInfo(configFileName, brokerName);
        Broker broker = new Broker(brokerConfig.getName(), brokerConfig.getBrokerIP(), brokerConfig.getBrokerPort());
        broker.run();
    }

    /**
     * method Creates Producer object and starts it
     * @param configFileName producerConfigFileName
     * @param producerName Name of Producer
     */
    public static void createAndStartProducer (String configFileName, String producerName) {
        ConfigInformation configInformation = Utility.extractConsumerOrPublisherConfigInfo(configFileName, producerName);
        Producer producer = new Producer(configInformation.getName(), configInformation.getBrokerIP(), configInformation.getBrokerPort());
        producer.startProducer();

        System.out.printf("\n[Now Sending Actual data]\n");
        try (BufferedReader bufferedReader = Utility.fileReaderInitializer(configInformation.getFileName())) {
            String eachLine = bufferedReader.readLine();
            System.out.printf("\n[eachLine : %s]\n", eachLine);
            String topic = null;
            while (eachLine != null) {
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
                } else {
                    topic = "Mac";
                }

                System.out.printf("\n[Now Sending Actual data] [Topic : %s]\n", topic);
                producer.send(topic, eachLine.getBytes());    // send data

                if (configInformation.getFileName().equals("Apache.log")) {
                    String[] messagePart = eachLine.split("] \\[");
                    if (messagePart.length != 1) {
                        topic = "Apache";
                        producer.send(topic, eachLine.getBytes());  // send data
                    }
                }

                eachLine = bufferedReader.readLine();   // reading next line from the file
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method Creates Consumer object and starts it
     * @param configFileName consumerConfigFileName
     * @param consumerName Name of consumer
     */
    public static void createAndStartConsumer (String configFileName, String consumerName) {
        ConfigInformation configInformation = Utility.extractConsumerOrPublisherConfigInfo(configFileName, consumerName);
        System.out.printf("\nConsumer Started\n");
        Consumer consumer = new Consumer(configInformation.getName(), configInformation.getType(), configInformation.getBrokerIP(), configInformation.getBrokerPort(), configInformation.getTopicName(), 0);
        try (FileOutputStream fileWriter = Utility.fileWriterInitializer(configInformation.getFileName())) {
            // Continue to pull messages...forever
            while(true) {
                byte[] message = consumer.poll(Duration.ofMillis(100));
                // writing data on file.
                if (message != null) {
                    fileWriter.write(message);
                    fileWriter.write((byte)'\n');
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
