import com.google.protobuf.InvalidProtocolBufferException;
import model.Constants;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import proto.Packet;

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
                    byte[] newMessage = newConnectionServerSide.receive();
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

        assertTrue(newConnectionClientSide.send(textByte));
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
                    byte[] newMessage = newConnectionServerSide.receive();
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

        assertTrue(newConnectionClientSide.send(textByte));
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
                    byte[] newMessage = newConnectionServerSide.receive();
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
        newConnectionClientSide.send(text);
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
                    byte[] newMessage = newConnectionServerSide.receive();
                    if (newMessage != null) {
                        try {
                            Packet.PacketDetails newPacket = Packet.PacketDetails.parseFrom(newMessage);
                            Assertions.assertEquals(Constants.INITIAL_SETUP, newPacket.getType());
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

        Producer producer = new Producer("PRODUCER-1", "localhost", 8007);
        byte[] initialMessagePacket = producer.createInitialMessagePacket1();
        newConnectionClientSide.send(initialMessagePacket);
    }
}
