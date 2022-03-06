public class RequestProcessor implements Runnable {
    private Connection connection;
    private Data data;

    /**
     * Constructor that initialises Connection class object and also Data
     * @param connection
     */
    public RequestProcessor(Connection connection) {
        this.connection = connection;
        this.data = DataInitializer.getData();
    }

    @Override
    public void run() {
        while (true) {

        }
    }

    public void start() {

    }
}
