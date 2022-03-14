/**
 * DataInitializer class make sure that only one instance of the Data class is created.
 * @author nilimajha
 */
public class DataInitializer {
    private static Data data = null;

    private DataInitializer() {}

    /**
     * Returns the Data object.
     * if the object has already been created then the reference of that object is passed.
     * if the object is not yet created then it will create an instance of it are return.
     * @return data
     */
    public synchronized static Data getData() {
        if (data == null) {
            data = new Data();
        }
        return data;
    }
}
