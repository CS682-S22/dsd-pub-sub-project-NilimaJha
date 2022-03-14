import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;

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
        // -name PRODUCER-1 -configFile ProducerInfoConfig.json
        //broker ip port
        // -name BROKER-1 -configFile BrokerConfig.json
        //consumer outputFileName topicName
        // -name CONSUMER-1 -configFile ConsumerInfoConfig.json

        //validate args
        if (!Utility.argsIsValid(args)) {
            System.out.println("Argument provided is invalid.");
            System.exit(0);
        }
        //parseArgs
        String hostType = Utility.getNameFromArgs(args);
        String configFileName = Utility.getConfigFilename(args);

        if (hostType.equals(Constants.BROKER)) {
            BrokerConfig brokerConfig = Utility.extractBrokerConfigInfo(configFileName, hostType);
            Broker broker = new Broker(brokerConfig.getName(), brokerConfig.getBrokerIP(), brokerConfig.getBrokerPort());
            broker.run();
        } else if (hostType.equals(Constants.PRODUCER)) {
            ConfigInformation configInformation = Utility.extractConsumerOrPublisherConfigInfo(configFileName, hostType);
            Producer producer = new Producer(configInformation.getName(), configInformation.getBrokerIP(), configInformation.getBrokerPort());

            try (BufferedReader bufferedReader = Utility.fileReaderInitializer(configInformation.getFileName())) {
                String eachLine = bufferedReader.readLine();
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
        } else {
            ConfigInformation configInformation = Utility.extractConsumerOrPublisherConfigInfo(configFileName, hostType);
            Consumer consumer = new Consumer(configInformation.getName(), configInformation.getType(), configInformation.getBrokerIP(), configInformation.getBrokerPort(), configInformation.getTopicName(), 0);

            try (FileOutputStream fileWriter = Utility.fileWriterInitializer(configInformation.getFileName())) {
                // Continue to pull messages...forever
                while(true) {
                    byte[] message = consumer.poll(Duration.ofMillis(100));
                    // writing data on file.
                    fileWriter.write(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
