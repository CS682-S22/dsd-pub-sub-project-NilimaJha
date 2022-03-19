package model;

public class Constants {
    public final static int BUFFER_SIZE = 60000;
    public final static  int READ_TIMEOUT_TIME = 500;
    public final static  int TOTAL_IN_MEMORY_MESSAGE_SIZE = 10; // number of message after which it will be flushed on to the segment file.
    public final static String INITIAL_SETUP = "INITIAL_SETUP";
    public final static String PUBLISH_REQUEST = "PUBLISH";
    public final static String PULL_REQUEST = "PULL";
    public final static String PRODUCER = "PRODUCER";
    public final static String CONSUMER = "CONSUMER";
    public final static String BROKER = "BROKER";
    public final static String CONSUMER_PULL = "PULL";
    public final static String CONSUMER_PUSH = "PUSH";
    public final static String MESSAGE = "MESSAGE";
    public final static int MESSAGE_BATCH_SIZE = 10;
    public final static int FLUSH_FREQUENCY = 6000;
    public final static String MESSAGE_NOT_AVAILABLE = "MESSAGE_NOT_AVAILABLE";

}
