import broker.Broker;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import consumer.Consumer;
import customeException.ConnectionClosedException;
import loadBalancer.LoadBalancer;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import producer.Producer;
import proto.ConsumerPullRequest;
import proto.InitialMessage;
import proto.PublisherPublishMessage;
import util.Constants;

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
    static LoadBalancer loadBalancer = null;
    static Broker broker = null;
    static Producer producer = null;
    static Consumer consumer = null;

    /**
     * initialises the broker, producer, consumer.Consumer
     */
    @Before
    public void init() {
        System.out.println("inside init...");
        if (loadBalancer == null) {
            System.out.println("Initialising LoadBalancer...");
            loadBalancer = new LoadBalancer("Load-Balancer", "localhost", 8080);
            threadPool.execute(loadBalancer::startLoadBalancer);
        }

        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


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
            producer.setupConnectionWithLeaderBroker();

            consumer = new Consumer("CONSUMER-TEST", "PULL", "loadBalancer.LoadBalancer-1", "localhost", 9090, "Test", 0);

            System.out.println("producer started...");
        } else {
            System.out.println("broker.Broker is not null...");
        }
    }

    /**
     * Tests the type of packet createInitialMessage from producer.Producer Class.
     */
    @Test
    public void createProducerInitialMessagePacketTest1() {
        if (producer != null) {
            byte[] producerInitialMessagePacket = producer.createInitialMessagePacket1(1);
            try {
                Any any = Any.parseFrom(producerInitialMessagePacket);
                assertTrue(any.is(InitialMessage.InitialMessageDetails.class));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("1.producer.Producer is null [createProducerInitialMessagePacketTest1]");
        }
    }

    /**
     * Tests the sender filed of packet createInitialMessage from producer.Producer Class.
     */
    @Test
    public void createProducerInitialMessagePacketTest2() {
        if (producer != null) {
            byte[] producerInitialMessagePacket = producer.createInitialMessagePacket1(1);
            try {
                Any any = Any.parseFrom(producerInitialMessagePacket);
                InitialMessage.InitialMessageDetails initialMessageDetails = any.unpack(InitialMessage.InitialMessageDetails.class);
                assertEquals(Constants.PRODUCER, initialMessageDetails.getConnectionSender());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("2.producer.Producer is null [createProducerInitialMessagePacketTest2]");
        }
    }

    /**
     * Tests the name of the sender of packet createInitialMessage from producer.Producer Class.
     */
    @Test
    public void createProducerInitialMessagePacketTest3() {
        if (producer != null) {
            byte[] producerInitialMessagePacket = producer.createInitialMessagePacket1(1);
            try {
                Any any = Any.parseFrom(producerInitialMessagePacket);
                InitialMessage.InitialMessageDetails initialMessageDetails = any.unpack(InitialMessage.InitialMessageDetails.class);
                assertEquals(producer.getName(), initialMessageDetails.getName());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("3.producer.Producer is null [createProducerInitialMessagePacketTest3]");
        }
    }

    /**
     * tests the createPublishPacket type of ProducerClass.
     */
    @Test
    public void createPublishMessagePacketTest1() {
        if (producer != null) {
            byte[] messageToBePublished = "Test-Message-1".getBytes();
            String topic = "Test";
            byte[] publishMessagePacket = producer.createPublishMessagePacket(topic, messageToBePublished);
            try {
                Any any = Any.parseFrom(publishMessagePacket);
                PublisherPublishMessage.PublisherPublishMessageDetails publisherPublishMessageDetails
                        = any.unpack(PublisherPublishMessage.PublisherPublishMessageDetails.class);
                assertTrue(any.is(PublisherPublishMessage.PublisherPublishMessageDetails.class));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("4.producer.Producer is null [createPublishMessagePacketTest1]");
        }
    }

    /**
     * tests the createPublishPacket topic part of ProducerClass.
     */
    @Test
    public void createPublishMessagePacketTest2() {
        if (producer != null) {
            byte[] messageToBePublished = "Test-Message-1".getBytes();
            String topic = "Test";
            byte[] publishMessagePacket = producer.createPublishMessagePacket(topic, messageToBePublished);
            PublisherPublishMessage.PublisherPublishMessageDetails publisherPublishMessageDetails;
            try {
                Any any = Any.parseFrom(publishMessagePacket);
                publisherPublishMessageDetails = any.unpack(PublisherPublishMessage.PublisherPublishMessageDetails.class);
                assertEquals(topic, publisherPublishMessageDetails.getTopic());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("5.producer.Producer is null [createPublishMessagePacketTest2]");
        }
    }

    /**
     * tests the createPublishPacket message part of ProducerClass.
     */
    @Test
    public void createPublishMessagePacketTest3() {
        if (producer != null) {
            byte[] messageToBePublished = "Test-Message-1".getBytes();
            String topic = "Test";
            byte[] publishMessagePacket = producer.createPublishMessagePacket(topic, messageToBePublished);
            PublisherPublishMessage.PublisherPublishMessageDetails publisherPublishMessageDetails;
            try {
                Any any = Any.parseFrom(publishMessagePacket);
                publisherPublishMessageDetails = any.unpack(PublisherPublishMessage.PublisherPublishMessageDetails.class);
                assertEquals(new String(messageToBePublished), new String(publisherPublishMessageDetails.getMessage().toByteArray()));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("6.producer.Producer is null [createPublishMessagePacketTest3]");
        }
    }

    /**
     * test send Packet at the producer.
     */
    @Test
    public void producerSendPacketTest1() {
        if (broker != null && producer != null) {
            if (producer.isConnected()) {
                byte[] messageToBePublished = "Test-Message".getBytes();
                assertTrue(producer.send("Test", messageToBePublished));
            } else {
                System.out.println("9.a.Producer's connectionSocket is closed [producerSendPacketTest1]");
            }
        } else {
            System.out.println("9.b.Broker and Producer is null [producerSendPacketTest1]");
        }
    }

    /**
     * test the connectTo method of the model.Node class
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
        } else {
            System.out.println("10.Broker is null [consumerConnectToBrokerTest]");
        }
    }

    /**
     * test the initialMessagePacket from consumer.Consumer side.
     */
    @Test
    public void createConsumerInitialMessagePacketTest1() {
        if (consumer != null) {
            byte[] consumerInitialMessagePacket = consumer.createInitialMessagePacket();

            InitialMessage.InitialMessageDetails initialMessageDetails;
            try {
                Any any = Any.parseFrom(consumerInitialMessagePacket);
                initialMessageDetails = any.unpack(InitialMessage.InitialMessageDetails.class);
                assertEquals(consumer.getName(), initialMessageDetails.getName());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("11.consumer.Consumer is null [createConsumerInitialMessagePacketTest1]");
        }
    }

    /**
     * test the initialMessagePacket from consumer.Consumer side.
     */
    @Test
    public void createConsumerInitialMessagePacketTest2() {
        if (consumer != null) {
            byte[] consumerInitialMessagePacket = consumer.createInitialMessagePacket();

            InitialMessage.InitialMessageDetails initialMessageDetails;
            try {
                Any any = Any.parseFrom(consumerInitialMessagePacket);
                initialMessageDetails = any.unpack(InitialMessage.InitialMessageDetails.class);
                assertEquals(consumer.getNodeType(), initialMessageDetails.getConsumerType());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("12.consumer.Consumer is null [createConsumerInitialMessagePacketTest2]");
        }
    }

    /**
     * test createPullRequestMessagePacket at producer.Producer.
     */
    @Test
    public void createConsumerPullRequestMessagePacketTest4() {
        if (consumer != null) {
            byte[] consumerPullRequestPacket = consumer.createPullRequestMessagePacket();

            ConsumerPullRequest.ConsumerPullRequestDetails consumerPullRequestDetails;
            try {
                Any any = Any.parseFrom(consumerPullRequestPacket);
                consumerPullRequestDetails = any.unpack(ConsumerPullRequest.ConsumerPullRequestDetails.class);
                assertEquals(consumer.getTopic(), consumerPullRequestDetails.getTopic());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("14.consumer.Consumer is null [createConsumerPullRequestMessagePacketTest3]");
        }
    }

    /**
     * test createPullRequestMessagePacket at producer.Producer.
     */
    @Test
    public void createConsumerPullRequestMessagePacketTest5() {
        if (consumer != null) {
            byte[] consumerPullRequestPacket = consumer.createPullRequestMessagePacket();

            ConsumerPullRequest.ConsumerPullRequestDetails consumerPullRequestDetails;
            try {
                Any any = Any.parseFrom(consumerPullRequestPacket);
                consumerPullRequestDetails = any.unpack(ConsumerPullRequest.ConsumerPullRequestDetails.class);
                assertEquals(consumer.getOffset(), consumerPullRequestDetails.getOffset());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("15.consumer.Consumer is null [createConsumerPullRequestMessagePacketTest4]");
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
