import broker.HeartBeatModule;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import connection.Connection;
import customeException.ConnectionClosedException;
import model.BrokerInfo;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import producer.Producer;
import proto.ElectionResponseMessage;
import proto.InitialMessage;
import proto.InitialSetupDone;
import proto.PublisherPublishMessage;
import util.Utility;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionAndUtilTest {
    public static AsynchronousServerSocketChannel serverSocket = null;
    public static AsynchronousSocketChannel server = null;
    public static AsynchronousSocketChannel client = null;
    public static Future futureObjServer = null;
    public static Future futureObjClient = null;
    public static Connection newConnectionServerSide = null;
    public static Connection newConnectionClientSide = null;

    /**
     * test for argumentIsValid Method.
     */
    @Test
    public void testArgumentIsValid1() {
        String[] args = {"-type", "PRODUCER", "-name", "PRODUCER-1", "-configFile", "ProducerInfoConfig.json"};
        assertTrue(Utility.argsIsValid(args));
    }

    /**
     * test for argumentIsValid Method.
     */
    @Test
    public void testArgumentIsValid2() {
        String[] args = {"-type", "CONSUMER", "-name", "CONSUMER-1", "-configFile", "ConsumerInfoConfig.json"};
        assertTrue(Utility.argsIsValid(args));
    }

    /**
     * test for argumentIsValid Method.
     */
    @Test
    public void testArgumentIsValid3() {
        String[] args = {"-type", "BROKER", "-name", "BROKER-1", "-configFile", "BrokerConfig.json"};
        assertTrue(Utility.argsIsValid(args));
    }

    /**
     * test for argumentIsValid Method.
     */
    @Test
    public void testArgumentIsValid4() {
        String[] args = {"-type", "BROKER", "-name", "ABC", "-configFile", "BrokerConfig.json"};
        assertTrue(Utility.argsIsValid(args));
    }

    /**
     * test for argumentIsValid Method.
     */
    @Test
    public void testArgumentIsValid5() {
        String[] args = {"-name", "BROKER-1", "-type", "BROKER", "-configFile", "BrokerConfig.json"};
        assertFalse(Utility.argsIsValid(args));
    }

    /**
     * test for argumentIsValid Method.
     */
    @Test
    public void testArgumentIsValid6() {
        String[] args = {"-name", "BROKER", "-type", "BROKER-1", "BrokerConfig.json", "-configFile"};
        assertFalse(Utility.argsIsValid(args));
    }

    /**
     * test for argumentIsValid Method.
     */
    @Test
    public void testArgumentIsValid7() {
        String[] args = {"-type", "BROKER", "-configFile", "BrokerConfig.json"};
        assertFalse(Utility.argsIsValid(args));
    }

    /**
     * test for typeIsValid Method.
     */
    @Test
    public void testTypeIsValid1() {
        String type = "BROKER";
        assertTrue(Utility.typeIsValid(type));
    }

    /**
     * test for typeIsValid Method.
     */
    @Test
    public void testTypeIsValid2() {
        String type = "PRODUCER";
        assertTrue(Utility.typeIsValid(type));
    }

    /**
     * test for typeIsValid Method.
     */
    @Test
    public void testTypeIsValid3() {
        String type = "CONSUMER";
        assertTrue(Utility.typeIsValid(type));
    }

    /**
     * test for typeIsValid Method.
     */
    @Test
    public void testTypeIsValid4() {
        String type = "BROKER1";
        assertFalse(Utility.typeIsValid(type));
    }

    /**
     * test for typeIsValid Method.
     */
    @Test
    public void testTypeIsValid5() {
        String type = "producer";
        assertFalse(Utility.typeIsValid(type));
    }

    /**
     * test for typeIsValid Method.
     */
    @Test
    public void testTypeIsValid6() {
        String type = "CONSUMER ";
        assertFalse(Utility.typeIsValid(type));
    }

    /**
     * test for typeIsValid Method.
     */
    @Test
    public void testTypeIsValid7() {
        String type = "LOAD-BALANCER";
        assertTrue(Utility.typeIsValid(type));
    }

    /**
     * test for fileNameIsValid Method.
     */
    @Test
    public void testFileNameIsValid1() {
        String fileName = "BrokerConfig.json";
        assertTrue(Utility.fileNameIsValid(fileName));
    }

    /**
     * test for fileNameIsValid Method.
     */
    @Test
    public void testFileNameIsValid2() {
        String fileName = "config.json";
        assertTrue(Utility.fileNameIsValid(fileName));
    }

    /**
     * test for fileNameIsValid Method.
     */
    @Test
    public void testFileNameIsValid3() {
        String fileName = "BrokerConfig.json ";
        assertFalse(Utility.fileNameIsValid(fileName));
    }

    /**
     * test for fileNameIsValid Method.
     */
    @Test
    public void testFileNameIsValid4() {
        String fileName = "BrokerConfig.txt ";
        assertFalse(Utility.fileNameIsValid(fileName));
    }

    /**
     * test for fileNameIsValid Method.
     */
    @Test
    public void testFileNameIsValid5() {
        String fileName = "BrokerConfig";
        assertFalse(Utility.fileNameIsValid(fileName));
    }

    /**
     * test for getTypeFromArgs Method.
     */
    @Test
    public void testGetTypeFromArgs() {
        String[] args = {"-type", "BROKER", "-name", "BROKER-1", "-configFile", "BrokerConfig.json"};
        assertEquals(args[1], Utility.getTypeFromArgs(args));
    }

    /**
     * test for getNameFromArgs Method.
     */
    @Test
    public void testGetNameFromArgs() {
        String[] args = {"-type", "BROKER", "-name", "BROKER-1", "-configFile", "BrokerConfig.json"};
        assertEquals(args[3], Utility.getNameFromArgs(args));
    }

    /**
     * test for getNameFromArgs Method.
     */
    @Test
    public void testGetConfigFilename() {
        String[] args = {"-type", "BROKER", "-name", "BROKER-1", "-configFile", "BrokerConfig.json"};
        assertEquals(args[5], Utility.getConfigFilename(args));
    }

    @Test
    public void createInitialMessagePacketTest() {
        Producer producer = new Producer("PRODUCER-1", "LOAD-BALANCER-TEST", "localhost", 9090, "TEST");
        byte[] message = producer.createInitialMessagePacket1(0);
        try {
            Any any = Any.parseFrom(message);
            assertTrue(any.is(InitialMessage.InitialMessageDetails.class));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createInitialMessagePacketTest2() {
        Producer producer = new Producer("PRODUCER-1", "LOAD-BALANCER-TEST", "localhost", 9090, "TEST");
        byte[] message = producer.createInitialMessagePacket1(0);
        try {
            Any any = Any.parseFrom(message);
            assertFalse(any.is(InitialSetupDone.InitialSetupDoneDetails.class));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createPublishMessagePacket1() {
        Producer producer = new Producer("PRODUCER-1", "LOAD-BALANCER-TEST", "localhost", 9090, "TEST");
        byte[] data = "Test Data".getBytes();
        byte[] message = producer.createPublishMessagePacket("Test", data);
        try {
            Any any = Any.parseFrom(message);
            assertTrue(any.is(PublisherPublishMessage.PublisherPublishMessageDetails.class));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createPublishMessagePacket2() {
        Producer producer = new Producer("PRODUCER-1", "LOAD-BALANCER-TEST", "localhost", 9090, "TEST");
        byte[] data = "Test Data".getBytes();
        byte[] message = producer.createPublishMessagePacket("Test", data);
        try {
            Any any = Any.parseFrom(message);
            assertFalse(any.is(ElectionResponseMessage.ElectionResponseMessageDetails.class));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    /**
     * tests send method of the connection class
     */
    @Test
    public void testConnectionSend1() {
        // setting up server
        Thread serverThread = new Thread() {
            @Override
            public void run() {
                try {
                    serverSocket = AsynchronousServerSocketChannel.open();
                    serverSocket.bind(new InetSocketAddress("localhost", 8004));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                futureObjServer = serverSocket.accept();
                try {
                    server = (AsynchronousSocketChannel) futureObjServer.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                newConnectionServerSide = new Connection(server);
                while (true) {
                    try {
                        byte[] newMessage = newConnectionServerSide.receive();
                    } catch (ConnectionClosedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        //starting server on separate thread
        serverThread.start();

        try {
            Thread.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // setting up client
        try {
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        futureObjClient = client.connect(new InetSocketAddress("localhost", 8004));
        try {
            futureObjClient.get();
            newConnectionClientSide = new Connection(client);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String text = "Some Random Text";
        byte[] textByte = text.getBytes();
        boolean result = false;
        try {
            result = newConnectionClientSide.send(textByte);
        } catch (ConnectionClosedException e) {
            e.printStackTrace();
        }

        assertTrue(result);
    }

    /**
     * tests send method of the connection class
     */
    @Test
    public void testConnectionSend2() {
        boolean shutdown = false;
        // setting up server
        Thread serverThread = new Thread() {
            @Override
            public void run() {
                try {
                    serverSocket = AsynchronousServerSocketChannel.open();
                    serverSocket.bind(new InetSocketAddress("localhost", 8005));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                futureObjServer = serverSocket.accept();
                try {
                    server = (AsynchronousSocketChannel) futureObjServer.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                newConnectionServerSide = new Connection(server);
                while (true) {
                    try {
                        byte[] newMessage = newConnectionServerSide.receive();
                    } catch (ConnectionClosedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        };

        //starting server on separate thread
        serverThread.start();

        try {
            Thread.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // setting up client
        try {
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        futureObjClient = client.connect(new InetSocketAddress("localhost", 8005));
        try {
            futureObjClient.get();
            newConnectionClientSide = new Connection(client);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String text = "Some Random Text";
        byte[] textByte = text.getBytes();

        boolean result = false;
        try {
            result = newConnectionClientSide.send(textByte);
        } catch (ConnectionClosedException e) {
            e.printStackTrace();
        }
        assertTrue(result);
    }

    /**
     * tests receive method of the connection class
     */
    @Test
    public void testConnectionReceive1() {
        boolean shutdown = false;
        // setting up server
        Thread serverThread = new Thread() {
            @Override
            public void run() {
                try {
                    serverSocket = AsynchronousServerSocketChannel.open();
                    serverSocket.bind(new InetSocketAddress("localhost", 8006));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                futureObjServer = serverSocket.accept();
                try {
                    server = (AsynchronousSocketChannel) futureObjServer.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                newConnectionServerSide = new Connection(server);
                while (true) {
                    byte[] newMessage = new byte[0];
                    try {
                        newMessage = newConnectionServerSide.receive();
                    } catch (ConnectionClosedException e) {
                        e.printStackTrace();
                    }
                    if (newMessage != null) {
                        Assertions.assertEquals("Some Random Text", new String(newMessage));
                        break;
                    }
                }
            }
        };

        //starting server on separate thread
        serverThread.start();

        try {
            Thread.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // setting up client
        try {
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        futureObjClient = client.connect(new InetSocketAddress("localhost", 8006));
        try {
            futureObjClient.get();
            newConnectionClientSide = new Connection(client);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        byte[] text = "Some Random Text".getBytes();
        try {
            newConnectionClientSide.send(text);
        } catch (ConnectionClosedException e) {
            e.printStackTrace();
        }
    }

    /**
     * tests receive method of the connection class
     */
    @Test
    public void testConnectionReceive2() {
        boolean shutdown = false;
        // setting up server
        Thread serverThread = new Thread() {
            @Override
            public void run() {
                try {
                    serverSocket = AsynchronousServerSocketChannel.open();
                    serverSocket.bind(new InetSocketAddress("localhost", 8007));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                futureObjServer = serverSocket.accept();
                try {
                    server = (AsynchronousSocketChannel) futureObjServer.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                newConnectionServerSide = new Connection(server);
                while (true) {
                    byte[] newMessage = new byte[0];
                    try {
                        newMessage = newConnectionServerSide.receive();
                    } catch (ConnectionClosedException e) {
                        e.printStackTrace();
                    }
                    if (newMessage != null) {
                        try {
                            Any any = Any.parseFrom(newMessage);
                            Assertions.assertTrue(any.is(InitialMessage.InitialMessageDetails.class));
                            break;
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        //starting server on separate thread
        serverThread.start();

        try {
            Thread.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // setting up client
        try {
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        futureObjClient = client.connect(new InetSocketAddress("localhost", 8007));
        try {
            futureObjClient.get();
            newConnectionClientSide = new Connection(client);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Producer producer = new Producer("PRODUCER-1", "LOAD-BALANCER", "localhost", 8007);
        byte[] initialMessagePacket = producer.createInitialMessagePacket1(0);
        try {
            newConnectionClientSide.send(initialMessagePacket);
        } catch (ConnectionClosedException e) {
            e.printStackTrace();
        }
    }
}
