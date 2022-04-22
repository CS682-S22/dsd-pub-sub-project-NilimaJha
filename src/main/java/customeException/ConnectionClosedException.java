package customeException;

/**
 * Custom Exception handler
 * @author nilimajha
 */
public class ConnectionClosedException extends Exception{
    public ConnectionClosedException(String message) {
        super(message);
    }
}
