package webSocketMessages.serverMessages;

public class Notification extends ServerMessage {

    private String message;

    public Notification() {
        super(ServerMessageType.NOTIFICATION);
    }

    public Notification(String message) {
        this();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
