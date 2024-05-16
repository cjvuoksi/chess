package webSocketMessages.serverMessages;

public class Error extends ServerMessage {

    public Error(String message) {
        super(ServerMessageType.ERROR);
        this.message = message;
    }
}
