package customeException;

public class ConnectionClosedException extends Exception{
    public ConnectionClosedException(String message) {
        super(message);
    }
}
