import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import customeException.ConnectionClosedException;
import model.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import proto.ConsumerPullRequest;
import proto.InitialMessage;
import proto.PublisherPublishMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class
 * @author nilimajha
 */
public class BrokerProducerAndConsumerTest {
    static ExecutorService threadPool = Executors.newFixedThreadPool(15);
    static Broker broker = null;
    static Producer producer = null;
    static Consumer consumer = null;

    /**
     * initialises the broker, producer, Consumer
     */
    @Before
    public void init() {
        System.out.println("inside init...");
        if (broker == null) {
            System.out.println("Initialising Broker...");
            broker = new Broker("BROKER-TEST", "localhost", 9090, "Load-Balancer", "localhost", 8080);
            threadPool.execute(broker);

            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            producer = new Producer("PRODUCER-TEST", "LB-1", "localhost", 9090);
            producer.startProducer();

            consumer = new Consumer("CONSUMER-TEST", "PULL", "LoadBalancer-1", "localhost", 9090, "Test", 0);

            System.out.println("producer started...");
        } else {
            System.out.println("Broker is not null...");
        }
    }

//    /**
//     * Tests the type of packet createInitialMessage from Producer Class.
//     */
//    @Test
//    public void createProducerInitialMessagePacketTest1() {
//        if (producer != null) {
//            byte[] producerInitialMessagePacket = producer.createInitialMessagePacket1();
//            Packet.PacketDetails packetDetails;
//            try {
//                packetDetails = Packet.PacketDetails.parseFrom(producerInitialMessagePacket);
//                assertEquals(Constants.INITIAL_SETUP, packetDetails.getType());
//                System.out.println("1.Success.");
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("1.Producer is null [createProducerInitialMessagePacketTest1]");
//        }
//    }

//    /**
//     * Tests the sender filed of packet createInitialMessage from Producer Class.
//     */
//    @Test
//    public void createProducerInitialMessagePacketTest2() {
//        if (producer != null) {
//            byte[] producerInitialMessagePacket = producer.createInitialMessagePacket1();
//            Packet.PacketDetails packetDetails;
//            InitialMessage.InitialMessageDetails initialMessageDetails;
//            try {
//                packetDetails = Packet.PacketDetails.parseFrom(producerInitialMessagePacket);
//                initialMessageDetails = InitialMessage.InitialMessageDetails.parseFrom(packetDetails.getMessage().toByteArray());
//                assertEquals(Constants.PRODUCER, initialMessageDetails.getConnectionSender());
//                System.out.println("2.Success.");
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("2.Producer is null [createProducerInitialMessagePacketTest2]");
//        }
//    }

//    /**
//     * Tests the name of the sender of packet createInitialMessage from Producer Class.
//     */
//    @Test
//    public void createProducerInitialMessagePacketTest3() {
//        if (producer != null) {
//            byte[] producerInitialMessagePacket = producer.createInitialMessagePacket1();
//            Packet.PacketDetails packetDetails;
//            InitialMessage.InitialMessageDetails initialMessageDetails;
//            try {
//                packetDetails = Packet.PacketDetails.parseFrom(producerInitialMessagePacket);
//                initialMessageDetails = InitialMessage.InitialMessageDetails.parseFrom(packetDetails.getMessage().toByteArray());
//                assertEquals(producer.getName(), initialMessageDetails.getName());
//                System.out.println("3.Success.");
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("3.Producer is null [createProducerInitialMessagePacketTest3]");
//        }
//    }

//    /**
//     * tests the createPublishPacket type of ProducerClass.
//     */
//    @Test
//    public void createPublishMessagePacketTest1() {
//        if (producer != null) {
//            byte[] messageToBePublished = "Test-Message-1".getBytes();
//            String topic = "Test";
//            byte[] publishMessagePacket = producer.createPublishMessagePacket(topic, messageToBePublished);
//            Packet.PacketDetails packetDetails;
//            try {
//                packetDetails = Packet.PacketDetails.parseFrom(publishMessagePacket);
//                assertEquals(Constants.PUBLISH_REQUEST, packetDetails.getType());
//                System.out.println("4.Success.");
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("4.Producer is null [createPublishMessagePacketTest1]");
//        }
//    }

//    /**
//     * tests the createPublishPacket topic part of ProducerClass.
//     */
//    @Test
//    public void createPublishMessagePacketTest2() {
//        if (producer != null) {
//            byte[] messageToBePublished = "Test-Message-1".getBytes();
//            String topic = "Test";
//            byte[] publishMessagePacket = producer.createPublishMessagePacket(topic, messageToBePublished);
//            Packet.PacketDetails packetDetails;
//            PublisherPublishMessage.PublisherPublishMessageDetails publisherPublishMessageDetails;
//            try {
//                packetDetails = Packet.PacketDetails.parseFrom(publishMessagePacket);
//                publisherPublishMessageDetails = PublisherPublishMessage.PublisherPublishMessageDetails.parseFrom(packetDetails.getMessage().toByteArray());
//                assertEquals(topic, publisherPublishMessageDetails.getTopic());
//                System.out.println("5.Success.");
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("5.Producer is null [createPublishMessagePacketTest2]");
//        }
//    }

//    /**
//     * tests the createPublishPacket message part of ProducerClass.
//     */
//    @Test
//    public void createPublishMessagePacketTest3() {
//        if (producer != null) {
//            byte[] messageToBePublished = "Test-Message-1".getBytes();
//            String topic = "Test";
//            byte[] publishMessagePacket = producer.createPublishMessagePacket(topic, messageToBePublished);
//            Packet.PacketDetails packetDetails;
//            PublisherPublishMessage.PublisherPublishMessageDetails publisherPublishMessageDetails;
//            try {
//                packetDetails = Packet.PacketDetails.parseFrom(publishMessagePacket);
//                publisherPublishMessageDetails = PublisherPublishMessage.PublisherPublishMessageDetails.parseFrom(packetDetails.getMessage().toByteArray());
//                assertEquals(new String(messageToBePublished), new String(publisherPublishMessageDetails.getMessage().toByteArray()));
//                System.out.println("6.Success.");
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("6.Producer is null [createPublishMessagePacketTest3]");
//        }
//    }

    /**
     * test send Packet at the producer.
     */
    @Test
    public void producerSendPacketTest1() {
        if (broker != null && producer != null) {
            if (producer.connection.connectionIsOpen()) {
                byte[] messageToBePublished = "Test-Message".getBytes();
                assertTrue(producer.send("Test", messageToBePublished));
                System.out.println("9.Success.");
            } else {
                System.out.println("9.a.Producer producer.connection.connectionSocket is closed [producerSendPacketTest1]");
            }
        } else {
            System.out.println("9.b.Broker and Producer is null [producerSendPacketTest1]");
        }
    }

    /**
     * test the connectTo method of the Node class
     */
    @Test
    public void consumerConnectToBrokerTest() {
        if (broker != null) {
            boolean connected = false;
            try {
                connected = consumer.connectToBroker();
            } catch (ConnectionClosedException e) {
                e.printStackTrace();
            }
            assertTrue(connected);
            System.out.println("10.Success.");
        } else {
            System.out.println("10.Broker is null [consumerConnectToBrokerTest]");
        }
    }

//    /**
//     * test the initialMessagePacket from Consumer side.
//     */
//    @Test
//    public void createConsumerInitialMessagePacketTest1() {
//        if (consumer != null) {
//            byte[] consumerInitialMessagePacket = consumer.createInitialMessagePacket();
//
//            Packet.PacketDetails packetDetails;
//            InitialMessage.InitialMessageDetails initialMessageDetails;
//            try {
//                packetDetails = Packet.PacketDetails.parseFrom(consumerInitialMessagePacket);
//                initialMessageDetails = InitialMessage.InitialMessageDetails.parseFrom(packetDetails.getMessage().toByteArray());
//                assertEquals(consumer.getName(), initialMessageDetails.getName());
//                System.out.println("11.Success.");
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("11.Consumer is null [createConsumerInitialMessagePacketTest1]");
//        }
//    }

//    /**
//     * test the initialMessagePacket from Consumer side.
//     */
//    @Test
//    public void createConsumerInitialMessagePacketTest2() {
//        if (consumer != null) {
//            byte[] consumerInitialMessagePacket = consumer.createInitialMessagePacket();
//
//            Packet.PacketDetails packetDetails;
//            InitialMessage.InitialMessageDetails initialMessageDetails;
//            try {
//                packetDetails = Packet.PacketDetails.parseFrom(consumerInitialMessagePacket);
//                initialMessageDetails = InitialMessage.InitialMessageDetails.parseFrom(packetDetails.getMessage().toByteArray());
//                assertEquals(consumer.getConsumerType(), initialMessageDetails.getConsumerType());
//                System.out.println("12.Success.");
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("12.Consumer is null [createConsumerInitialMessagePacketTest2]");
//        }
//    }

//    /**
//     * test createPullRequestMessagePacket at Producer.
//     */
//    @Test
//    public void createConsumerPullRequestMessagePacketTest3() {
//        if (consumer != null) {
//            byte[] consumerPullRequestPacket = consumer.createPullRequestMessagePacket();
//
//            Packet.PacketDetails packetDetails;
//            try {
//                packetDetails = Packet.PacketDetails.parseFrom(consumerPullRequestPacket);
//                assertEquals(consumer.getConsumerType(), packetDetails.getType());
//                System.out.println("13.Success.");
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("13.Consumer is null [createConsumerPullRequestMessagePacketTest3]");
//        }
//    }

//    /**
//     * test createPullRequestMessagePacket at Producer.
//     */
//    @Test
//    public void createConsumerPullRequestMessagePacketTest4() {
//        if (consumer != null) {
//            byte[] consumerPullRequestPacket = consumer.createPullRequestMessagePacket();
//
//            Packet.PacketDetails packetDetails;
//            ConsumerPullRequest.ConsumerPullRequestDetails consumerPullRequestDetails;
//            try {
//                packetDetails = Packet.PacketDetails.parseFrom(consumerPullRequestPacket);
//                consumerPullRequestDetails = ConsumerPullRequest.ConsumerPullRequestDetails.parseFrom(packetDetails.getMessage().toByteArray());
//                assertEquals(consumer.getTopic(), consumerPullRequestDetails.getTopic());
//                System.out.println("14.Success.");
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("14.Consumer is null [createConsumerPullRequestMessagePacketTest3]");
//        }
//    }

//    /**
//     * test createPullRequestMessagePacket at Producer.
//     */
//    @Test
//    public void createConsumerPullRequestMessagePacketTest5() {
//        if (consumer != null) {
//            byte[] consumerPullRequestPacket = consumer.createPullRequestMessagePacket();
//
//            Packet.PacketDetails packetDetails;
//            ConsumerPullRequest.ConsumerPullRequestDetails consumerPullRequestDetails;
//            try {
//                packetDetails = Packet.PacketDetails.parseFrom(consumerPullRequestPacket);
//                consumerPullRequestDetails = ConsumerPullRequest.ConsumerPullRequestDetails.parseFrom(packetDetails.getMessage().toByteArray());
//                assertEquals(consumer.getOffset(), consumerPullRequestDetails.getOffset());
//                System.out.println("15.Success.");
//            } catch (InvalidProtocolBufferException e) {
//                e.printStackTrace();
//            }
//        } else {
//            System.out.println("15.Consumer is null [createConsumerPullRequestMessagePacketTest4]");
//        }
//    }

    /**
     * Pulling messages from broker.
     */
    @Test
    public void receiveMessageFromBroker1() {
        if (consumer != null && producer != null && producer.connection.connectionIsOpen() && broker != null) {
            for(int i = 0; i < 11; i++) {
                String message = "Test-Message";
                byte[] messageBytes = message.getBytes();
                producer.send("Test", messageBytes);
            }
            assertTrue(consumer.pullMessageFromBroker());
            System.out.println("16.Success.");
        } else {
            System.out.println("16.Condition is false [receiveMessageFromBroker1]");
        }
    }

    /**
     * Tests the createInitialMessage from Consumer Class.
     */
    @Test
    public void consumerSendInitialPacketToBrokerTest() {
        if (broker != null && consumer.connection.connectionIsOpen()) {
            assertTrue(consumer.sendInitialSetupMessage());
            System.out.println("18.Success.");
        } else {
            System.out.println("18.CONSUMER IS NOT YET CONNECTED TO BROKER.");
        }
    }

    /**
     * waits for 30000 millis after the execution of the entire function.
     */
    @AfterAll
    static void end() {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
