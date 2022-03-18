import model.BrokerConfig;
import model.ConfigInformation;
import model.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author nilimajha
 */
public class Driver {

    /**
     *
     * @param args
     */
    public static void main (String[] args) {
        //producer fileName topic brokerIP brokerPort
        // -type PRODUCER -name PRODUCER-1 -configFile ProducerInfoConfig.json
        //broker ip port
        // -type BROKER -name BROKER-1 -configFile model.BrokerConfig.json
        //consumer outputFileName topicName
        // -type CONSUMER -name CONSUMER-1 -configFile ConsumerInfoConfig.json

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
            ConfigInformation configInformation = Utility.extractConsumerOrPublisherConfigInfo(configFileName, hostName);
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
        } else { //Consumer
            ConfigInformation configInformation = Utility.extractConsumerOrPublisherConfigInfo(configFileName, hostName);
            System.out.printf("\nConsumer Started\n");
            Consumer consumer = new Consumer(configInformation.getName(), configInformation.getType(), configInformation.getBrokerIP(), configInformation.getBrokerPort(), configInformation.getTopicName(), 0);

            ExecutorService pool = Executors.newFixedThreadPool(1); //thread pool of size 15
            pool.execute(consumer :: startConsumer);    // assigning consumer thread
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

    public static void createAndStartBroker (String configFileName, String hostName) {
        BrokerConfig brokerConfig = Utility.extractBrokerConfigInfo(configFileName, hostName);
        Broker broker = new Broker(brokerConfig.getName(), brokerConfig.getBrokerIP(), brokerConfig.getBrokerPort());
        broker.run();
    }
}
