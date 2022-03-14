public class Constants {
    final static int BUFFER_SIZE = 60000;
    final static  int READ_TIMEOUT_TIME = 50;
    final static  int TOTAL_IN_MEMORY_MESSAGE_SIZE = 10; // number of message after which it will be flushed on to the segment file.
    final static long POLL_TIMEOUT_TIME = 100;
    final static String INITIAL = "INITIAL";
    final static String REQUEST_TYPE_PUBLISH = "PUBLISH";
    final static String REQUEST_TYPE_PULL = "PULL";
    final static String PRODUCER = "PRODUCER";
    final static String CONSUMER = "CONSUMER";
    final static String BROKER = "BROKER";
    final static String CONSUMER_PULL = "CONSUMER_PULL";
    final static String CONSUMER_PUSH = "CONSUMER_PUSH";
    final static String CONSUMER_TYPE_PULL = "PULL";
    final static String CONSUMER_TYPE_PUSH = "PUSH";
//    final  static String FILE_NAME = "Segment File";
}
