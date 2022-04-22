import broker.Broker;
import broker.HeartBeatModule;
import consumer.Consumer;
import loadBalancer.LoadBalancer;
import model.*;
import org.junit.Before;
import org.junit.Test;
import producer.Producer;
import util.Constants;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  test class
 * @author nilimajha
 */
public class Project3Test {
    private BrokerInfo thisBrokerInfo = new BrokerInfo("TEST-BROKER", 1, "localhost", 8080);
    private HeartBeatModule heartBeatModule = HeartBeatModule.getHeartBeatModule(thisBrokerInfo, "localhost", 9090);
    static LoadBalancerDataStore loadBalancerDataStore = LoadBalancerDataStore.getLoadBalancerDataStore();
    static ExecutorService threadPool = Executors.newFixedThreadPool(15);
    static LoadBalancer loadBalancer = null;
    static Broker broker = null;
    static Producer producer = null;
    static Consumer consumer = null;

    /**
     * initialises the loadbalancer
     */
    @Before
    public void init() {
        System.out.println("inside init...");
        if (loadBalancer == null) {
            System.out.println("Initialising LoadBalancer...");
            loadBalancer = new LoadBalancer("LOAD-BALANCER", "localhost", 9090);
            threadPool.execute(loadBalancer::startLoadBalancer);

            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            broker = new Broker("TEST-BROKER1","localhost", 9060, "LOAD-BALANCER", "localhost", 9090);

            System.out.println("broker started...");
            threadPool.execute(broker);

            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            producer = new Producer("PRODUCER-TEST", "LOAD-BALANCER", "localhost", 9090);
            producer.setupConnectionWithLeaderBroker();

            System.out.println("producer started...");
        } else {
            System.out.println("Broker is not null...");
        }
    }


    /**
     * test for getID Method.
     */
    @Test
    public void testGetID() {
        assertTrue(loadBalancerDataStore.getId() > 0);
    }

    /**
     * test for getID Method.
     */
    @Test
    public void testGetID2() {
        int id = loadBalancerDataStore.getId();
        assertTrue(id < loadBalancerDataStore.getId());
    }

    /**
     * test for getID Method.
     */
    @Test
    public void testGetID3() {
        int id = loadBalancerDataStore.getId();
        assertTrue((id + 1) == loadBalancerDataStore.getId());
    }

    /**
     * test for getLeaderInfo method.
     */
    @Test
    public void testGetLeaderInfo() {
        BrokerInfo leaderBrokerInfo = loadBalancerDataStore.getLeaderInfo();
        if (loadBalancerDataStore.getMembershipInfo().size() == 0) {
            assertEquals(null, leaderBrokerInfo);
        }
    }

    /**
     * test for getRandomFollowerBrokerInfo method.
     */
    @Test
    public void testGetRandomFollowerBrokerInfo1() {
        BrokerInfo randomFollowerBrokerInfo = loadBalancerDataStore.getRandomFollowerBrokerInfo();
        if (loadBalancerDataStore.getMembershipInfo().size() > 0) {
            assertNotEquals(null, randomFollowerBrokerInfo);
        }
    }

    /**
     * test for getRandomFollowerBrokerInfo method.
     */
    @Test
    public void testGetRandomFollowerBrokerInfo2() {
        BrokerInfo randomFollowerBrokerInfo = loadBalancerDataStore.getRandomFollowerBrokerInfo();
        if (loadBalancerDataStore.getMembershipInfo().size() > 0) {
            assertNotEquals(null, randomFollowerBrokerInfo);
        }
    }

    /**
     * test for getMembershipInfo Method.
     */
    @Test
    public void testGetMembershipInfo1() {
        ArrayList<BrokerInfo> membersList = loadBalancerDataStore.getMembershipInfo();
        if (loadBalancerDataStore.getMembershipInfo().size() > 0) {
            assertFalse(membersList.isEmpty());
        }
    }

    /**
     * test for getMembershipInfo method.
     */
    @Test
    public void testGetMembershipInfo3() {
        BrokerInfo brokerInfo = new BrokerInfo("BROKER3", 3, "localhost", 8060);
        BrokerInfo brokerInfo2 = new BrokerInfo("BROKER4", 4, "localhost", 8065);
        MembershipTable membershipTable = MembershipTable.getMembershipTable("LOAD-BALANCER");
        membershipTable.addMember(3, brokerInfo);
        membershipTable.addMember(4, brokerInfo2);
        ArrayList<BrokerInfo> membersList = loadBalancerDataStore.getMembershipInfo();
        List<Integer> membersIdInMemberList = new ArrayList<>(membershipTable.getMembershipInfo().keySet());
        assertEquals(membersList.size(), membersIdInMemberList.size());
    }

    /**
     * test for addNewMemberIntoMembershipTable method.
     */
    @Test
    public void testAddNewMemberIntoMembershipTable() {
        MembershipTable membershipTable = MembershipTable.getMembershipTable("LOAD-BALANCER");
        loadBalancerDataStore.addNewMemberIntoMembershipTable(5, "BROKER5", "localhost", 8050);
        ArrayList<BrokerInfo> membersList = loadBalancerDataStore.getMembershipInfo();
        List<Integer> membersIdInMemberList = new ArrayList<>(membershipTable.getMembershipInfo().keySet());
        assertEquals(membersList.size(), membersIdInMemberList.size());
    }

    /**
     * test to mark down a member from a membership table.
     */
    @Test
    public void testMarkDown() {
        loadBalancerDataStore.addNewMemberIntoMembershipTable(6, "BROKER6", "localhost", 8052);
        assertTrue(loadBalancerDataStore.markMemberDown(6));
    }

    /**
     * test to mark down a member from a membership table.
     */
    @Test
    public void testMarkDown2() {
        loadBalancerDataStore.addNewMemberIntoMembershipTable(7, "BROKER7", "localhost", 8054);
        ArrayList<BrokerInfo> membersListBefore = loadBalancerDataStore.getMembershipInfo();
        loadBalancerDataStore.markMemberDown(7);
        ArrayList<BrokerInfo> membersListAfter = loadBalancerDataStore.getMembershipInfo();
        assertEquals((membersListBefore.size() - 1), membersListAfter.size());
    }

    /**
     * test for the method updateHeartBeat in HeartBeatModule.
     */
    @Test
    public void testUpdateHeartBeat1() {
        assertTrue(heartBeatModule.updateHeartBeat(1));
    }

    /**
     * test for the method updateHeartBeat in HeartBeatModule.
     */
    @Test
    public void testUpdateHeartBeat2() {
        assertTrue(heartBeatModule.updateHeartBeat(2));
    }

    /**
     * test for the method markMemberFailed in HeartBeatModule.
     */
    @Test
    public void testMarkMemberFailed1() {
        heartBeatModule.markMemberFailed(3);
        assertTrue(heartBeatModule.markMemberFailed(3));
    }

    /**
     * test for the method markMemberFailed in HeartBeatModule.
     */
    @Test
    public void testMarkMemberFailed2() {
        heartBeatModule.markMemberFailed(3);
        assertTrue(heartBeatModule.markMemberFailed(3));
    }

    /**
     * test for producer send method.
     */
    @Test
    public void createProducerInitialMessagePacketTest1() {
        if (producer != null && loadBalancer != null) {
            assertTrue(producer.send("Test", "Test-Message".getBytes()));
        }
    }

    /**
     * test for consumer poll method.
     */
    @Test
    public void createProducerInitialMessagePacketTest2() {
        if (consumer != null && loadBalancer != null) {
            consumer = new Consumer("CONSUMER-TEST", "PULL", "LOAD-BALANCER", "localhost", 9090, "Test", 0);
            assertTrue(consumer.poll(Duration.ofMillis(100)) == null || consumer.poll(Duration.ofMillis(100)).length > 0);
        }
    }

    /**
     * test for topic snapshot.
     */
    @Test
    public void testTopicSnapshot() {
        TopicSnapshot topicSnapshot = new TopicSnapshot("TEST", 12345);
        assertEquals("TEST", topicSnapshot.getTopic());
    }

    /**
     * test for topic snapshot.
     */
    @Test
    public void testTopicSnapshot1() {
        TopicSnapshot topicSnapshot = new TopicSnapshot("TEST", 12345);
        assertNotEquals("Test", topicSnapshot.getTopic());
    }

    /**
     * test for topic snapshot.
     */
    @Test
    public void testTopicSnapshot2() {
        TopicSnapshot topicSnapshot = new TopicSnapshot("TEST", 12345);
        assertEquals(12345, topicSnapshot.getOffset());
    }

    /**
     * test for topic snapshot.
     */
    @Test
    public void testTopicSnapshot3() {
        TopicSnapshot topicSnapshot = new TopicSnapshot("TEST", 12345);
        topicSnapshot.setTopic("TEST2");
        assertNotEquals("TEST", topicSnapshot.getTopic());
    }

    /**
     * test for topic snapshot.
     */
    @Test
    public void testTopicSnapshot4() {
        TopicSnapshot topicSnapshot = new TopicSnapshot("TEST", 12345);
        topicSnapshot.setOffset(54321);
        assertNotEquals(12345, topicSnapshot.getOffset());
    }

    /**
     * test for DB snapshot.
     */
    @Test
    public void testDBSnapshot() {
        TopicSnapshot topic1Snapshot = new TopicSnapshot("TEST", 1111111);
        TopicSnapshot topic2Snapshot = new TopicSnapshot("TEST1", 222222222);
        TopicSnapshot topic3Snapshot = new TopicSnapshot("TEST2", 123456);
        TopicSnapshot topicSnapshot = new TopicSnapshot("TEST3", 654332);

        DBSnapshot dbSnapshot = new DBSnapshot(1);
        assertEquals(1, dbSnapshot.getMemberId());
    }

    /**
     * test for DB snapshot.
     */
    @Test
    public void testDBSnapshot1() {
        TopicSnapshot topic1Snapshot = new TopicSnapshot("TEST", 1111111);
        TopicSnapshot topic2Snapshot = new TopicSnapshot("TEST1", 222222222);
        TopicSnapshot topic3Snapshot = new TopicSnapshot("TEST2", 123456);
        TopicSnapshot topicSnapshot = new TopicSnapshot("TEST3", 654332);

        DBSnapshot dbSnapshot = new DBSnapshot(1);
        assertEquals(0, dbSnapshot.getTopicSnapshotMap().size());
    }

    /**
     * test for DB snapshot.
     */
    @Test
    public void testDBSnapshot3() {
        TopicSnapshot topic1Snapshot = new TopicSnapshot("TEST", 1111111);
        TopicSnapshot topic2Snapshot = new TopicSnapshot("TEST1", 222222222);
        TopicSnapshot topic3Snapshot = new TopicSnapshot("TEST2", 123456);
        TopicSnapshot topic4Snapshot = new TopicSnapshot("TEST3", 654332);

        DBSnapshot dbSnapshot = new DBSnapshot(1);
        dbSnapshot.addTopicSnapshot("TEST", topic1Snapshot);
        dbSnapshot.addTopicSnapshot("TEST1", topic2Snapshot);
        dbSnapshot.addTopicSnapshot("TEST2", topic3Snapshot);
        dbSnapshot.addTopicSnapshot("TEST3", topic4Snapshot);
        assertEquals(4, dbSnapshot.getTopicSnapshotMap().size());
    }

    /**
     * test for DB snapshot.
     */
    @Test
    public void testDBSnapshot4() {
        TopicSnapshot topic1Snapshot = new TopicSnapshot("TEST", 1111111);
        TopicSnapshot topic2Snapshot = new TopicSnapshot("TEST1", 222222222);
        TopicSnapshot topic3Snapshot = new TopicSnapshot("TEST2", 123456);
        TopicSnapshot topic4Snapshot = new TopicSnapshot("TEST3", 654332);

        DBSnapshot dbSnapshot = new DBSnapshot(1);
        dbSnapshot.addTopicSnapshot("TEST", topic1Snapshot);
        dbSnapshot.addTopicSnapshot("TEST1", topic2Snapshot);
        dbSnapshot.addTopicSnapshot("TEST2", topic3Snapshot);
        dbSnapshot.addTopicSnapshot("TEST3", topic4Snapshot);
        assertEquals(123456, dbSnapshot.getTopicSnapshotMap().get("TEST2").getOffset());
    }

    /**
     * test for BrokerInfo class method.
     */
    @Test
    public void testBrokerInfo1() {
        BrokerInfo brokerInfo = new BrokerInfo("TEST-BROKER-1", 1, "localhost", 8060);
        assertEquals("TEST-BROKER-1", brokerInfo.getBrokerName());
    }

    /**
     * test for BrokerInfo class method.
     */
    @Test
    public void testBrokerInfo2() {
        BrokerInfo brokerInfo = new BrokerInfo("TEST-BROKER-1", 1, "localhost", 8060);
        assertTrue(brokerInfo.isInCatchupMode());
    }

    /**
     * test for BrokerInfo class method.
     */
    @Test
    public void testBrokerInfo3() {
        BrokerInfo brokerInfo = new BrokerInfo("TEST-BROKER-1", 1, "localhost", 8060);
        assertFalse(brokerInfo.isDataConnectionConnected());
    }

    /**
     * test for Data class method.
     */
    @Test
    public void testData1() {
        BrokerInfo thisBrokerInfo = new BrokerInfo("TEST-BROKER-2", 1, "localhost", 8065);
        Data data = Data.getData(thisBrokerInfo, "localhost", 9090);
        long topic1Offset = 0;
        long topic2Offset = 0;
        data.addMessageToTopic(Constants.SYNCHRONOUS, "Topic-1", "topic1_message1".getBytes(), topic1Offset);
        topic1Offset = topic1Offset + ("topic1_message1".getBytes().length);
        data.addMessageToTopic(Constants.SYNCHRONOUS, "Topic-2", "topic2_message1".getBytes(), topic2Offset);
        topic1Offset = topic1Offset + ("topic1_message1".getBytes().length);
        data.addMessageToTopic(Constants.SYNCHRONOUS, "Topic-1", "topic1_message2".getBytes(), topic1Offset);
        topic1Offset = topic1Offset + ("topic1_message2".getBytes().length);
        data.addMessageToTopic(Constants.SYNCHRONOUS, "Topic-1", "topic1_message3".getBytes(), topic1Offset);
        topic2Offset = topic2Offset + ("topic1_message2".getBytes().length);
        assertEquals(2, data.getTopicLists().size());
    }

    /**
     * test for Data class method.
     */
    @Test
    public void testData2() {
        BrokerInfo thisBrokerInfo = new BrokerInfo("TEST-BROKER-2", 1, "localhost", 8065);
        Data data = Data.getData(thisBrokerInfo, "localhost", 9090);
        long topic1Offset = 0;
        long topic2Offset = 0;
        data.addMessageToTopic(Constants.SYNCHRONOUS, "Topic-1", "topic1_message1".getBytes(), topic1Offset);
        topic1Offset = topic1Offset + ("topic1_message1".getBytes().length);
        data.addMessageToTopic(Constants.SYNCHRONOUS, "Topic-2", "topic2_message1".getBytes(), topic2Offset);
        topic2Offset = topic2Offset + ("topic1_message1".getBytes().length);
        data.addMessageToTopic(Constants.SYNCHRONOUS, "Topic-1", "topic1_message2".getBytes(), topic1Offset);
        topic1Offset = topic1Offset + ("topic1_message2".getBytes().length);
        data.addMessageToTopic(Constants.SYNCHRONOUS, "Topic-1", "topic1_message3".getBytes(), topic1Offset);
        topic1Offset = topic1Offset + ("topic1_message2".getBytes().length);
        assertTrue(topic1Offset <= data.getMessageInfoForTheTopic("Topic-1").getLastOffSet());
    }
}
