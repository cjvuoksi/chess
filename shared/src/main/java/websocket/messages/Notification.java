package websocket.messages;

public class Notification extends ServerMessage {

    public Notification() {
        super(ServerMessageType.NOTIFICATION);
    }

    public Notification(String message) {
        this();
        this.message = message;
    }
}
