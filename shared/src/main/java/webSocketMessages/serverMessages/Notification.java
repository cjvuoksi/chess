package webSocketMessages.serverMessages;

public class Notification extends ServerMessage {

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
