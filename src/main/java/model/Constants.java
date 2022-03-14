package model;

public class Constants {
    public final static int BUFFER_SIZE = 60000;
    public final static  int READ_TIMEOUT_TIME = 50;
    public final static  int TOTAL_IN_MEMORY_MESSAGE_SIZE = 10; // number of message after which it will be flushed on to the segment file.
    public final static long POLL_TIMEOUT_TIME = 100;
    public final static String INITIAL = "INITIAL";
    public final static String REQUEST_TYPE_PUBLISH = "PUBLISH";
    public final static String REQUEST_TYPE_PULL = "PULL";
    public final static String PRODUCER = "PRODUCER";
    public final static String CONSUMER = "CONSUMER";
    public final static String BROKER = "BROKER";
    public final static String CONSUMER_PULL = "CONSUMER_PULL";
    public final static String CONSUMER_PUSH = "CONSUMER_PUSH";
    public final static String CONSUMER_TYPE_PULL = "PULL";
    public final static String CONSUMER_TYPE_PUSH = "PUSH";
}
