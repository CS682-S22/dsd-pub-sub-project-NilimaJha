import model.BrokerConfig;
import model.ConfigInformation;
import model.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Application class
 * contains main method.
 * @author nilimajha
 */
public class Driver {
    private static final Logger LOGGER = (Logger) LogManager.getLogger(Driver.class);

    /**
     * Application's main
     * @param args
     */
    public static void main (String[] args) {
        //validate args
        if (!Utility.argsIsValid(args)) {
            LOGGER.error("[Thread Id : " + Thread.currentThread().getId() + "]  Argument provided is invalid. Exiting the system.");
            System.exit(0);
        }
        //parseArgs
        String hostType = Utility.getTypeFromArgs(args);
        String hostName = Utility.getNameFromArgs(args);
        String configFileName = Utility.getConfigFilename(args);

        if (hostType.equals(Constants.BROKER)) {
            BrokerConfig brokerConfig = Utility.extractBrokerConfigInfo(configFileName, hostName);
            Broker broker = new Broker(brokerConfig.getName(), brokerConfig.getBrokerIP(), brokerConfig.getBrokerPort());
            broker.run();
        } else if (hostType.equals(Constants.PRODUCER)) {
            ConfigInformation configInformation = Utility.extractConsumerOrPublisherConfigInfo(configFileName, hostName);
            Producer producer = new Producer(configInformation.getName(), configInformation.getBrokerIP(), configInformation.getBrokerPort());
            producer.startProducer();
            LOGGER.info("[Thread Id : " + Thread.currentThread().getId() + "] Producer started.");
            System.out.printf("\n[Thread Id : %s] Producer started.\n", Thread.currentThread().getId());
            try (BufferedReader bufferedReader = Utility.fileReaderInitializer(configInformation.getFileName())) {
                String eachLine = bufferedReader.readLine();
                String topic = null;
                while (eachLine != null) {
                    if (configInformation.getFileName().equals("Apache.log")) {
                        topic = "Apache";
                    } else if (configInformation.getFileName().equals("Zookeeper.log")) {
                        topic = "Zookeeper";
                    } else {
                        topic = "Mac";
                    }
                    producer.send(topic, eachLine.getBytes());    // send data
                    eachLine = bufferedReader.readLine();   // reading next line from the file
                }
            } catch (IOException e) {
                LOGGER.error("[Thread Id : " + Thread.currentThread().getId() + "]  Caught IOException : " + e);
            }
            producer.close();
        } else { //Consumer
            ConfigInformation configInformation = Utility.extractConsumerOrPublisherConfigInfo(configFileName, hostName);
            Consumer consumer = new Consumer(configInformation.getName(), configInformation.getType(), configInformation.getBrokerIP(), configInformation.getBrokerPort(), configInformation.getTopicName(), 0);
            ExecutorService pool = Executors.newFixedThreadPool(1); //thread pool of size 1
            pool.execute(consumer :: startConsumer);    // assigning consumer thread
            LOGGER.info("[Thread Id : " + Thread.currentThread().getId() + "] Producer started.");
            System.out.printf("\n[Thread Id : %s] Producer started.\n", Thread.currentThread().getId());

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
                LOGGER.error("[Thread Id : " + Thread.currentThread().getId() + "]  Caught IOException : " + e);
            }
        }
    }
}
