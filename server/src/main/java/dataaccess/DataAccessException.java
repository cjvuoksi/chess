package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception {

    public DataAccessException(String message) {
        super(message);
    }
}

//TODO make more exceptions that correspond to errors