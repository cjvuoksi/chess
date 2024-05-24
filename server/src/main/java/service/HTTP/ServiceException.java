package service.HTTP;

public class ServiceException extends Exception {

    private final int code;

    public ServiceException(String message, int code) {
        super(message);
        this.code = code;
    }

    public ServiceException(String message) {
        super(message);
        this.code = 500;
    }

    public int getCode() {
        return code;
    }
}
