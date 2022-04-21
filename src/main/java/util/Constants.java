package util;

/**
 * Constant class to hold various Constants.
 * @author nilimajha
 */
public class Constants {
    public final static int BUFFER_SIZE = 60000;
    public final static  int READ_TIMEOUT_TIME = 60000;
    public final static  int TOTAL_IN_MEMORY_MESSAGE_SIZE = 10; // number of message after which it will be flushed on to the segment file.
    public final static String INITIAL_SETUP = "INITIAL_SETUP";
    public final static String PUBLISH_REQUEST = "PUBLISH";
    public final static String PULL_REQUEST = "PULL";
    public final static String DATA = "DATA";
    public final static String PRODUCER = "PRODUCER";
    public final static String CONSUMER = "CONSUMER";
    public final static String BROKER = "BROKER";
    public final static String LOAD_BALANCER = "LOAD-BALANCER";
    public final static String CONSUMER_PULL = "PULL";
    public final static String CONSUMER_PUSH = "PUSH";
    public final static String MESSAGE = "MESSAGE";
    public final static int MESSAGE_BATCH_SIZE = 10;
    public final static int FLUSH_FREQUENCY = 6000;
    public final static int TIMEOUT_IF_DATA_NOT_YET_AVAILABLE = 6000;
    public final static String MESSAGE_NOT_AVAILABLE = "MESSAGE_NOT_AVAILABLE";
    public final static String TOPIC_NOT_AVAILABLE = "TOPIC_NOT_AVAILABLE";
    public final static int BROKER_THREAD_POOL_SIZE = 30;
    public final static int LOAD_BALANCER_THREAD_POOL_SIZE = 20;
    public final static String BROKEN_PIPE = "java.io.IOException: Broken pipe";
    public final static int MAX_RETRIES = 5;
    public final static int RETRIES_TIMEOUT = 30000;
    public final static long TIMEOUT_NANOS = 60000000000L;//2000000L;//300000000000L; // 1 milli = 1000000 nano.
    public final static int HEARTBEAT_CHECK_TIMER_TIMEOUT = 30000;
    public final static int HEARTBEAT_SEND_TIMER_TIMEOUT = 30000;
    public final static String HEARTBEAT_CONNECTION = "HEARTBEAT_CONNECTION";
    public final static String DATA_CONNECTION = "DATA_CONNECTION";
    public final static String SYNCHRONOUS = "SYNCHRONOUS";
    public final static String CATCHUP = "CATCHUP";
    public final static String CATCHUP_CONNECTION = "CATCHUP_CONNECTION";
    public final static String START_SYNC = "START_SYNC";
    public final static String DB_SNAPSHOT = "DB_SNAPSHOT";
}
