package ui;

public class SwitchException extends Exception {
    private final exceptionType type;
    private final String[] payload;

    public exceptionType getType() {
        return type;
    }

    public String[] getPayload() {
        return payload;
    }

    public enum exceptionType {
        EXIT,
        LOGIN,
        LOGOUT,
        LEAVE,
        PLAY,
        WATCH
    }

    public SwitchException(exceptionType type) {
        this.type = type;
        payload = null;
    }

    public SwitchException(exceptionType type, String ...payload) {
        this.type = type;
        this.payload = payload;
    }
}
