package webSocketMessages.serverMessages;

public class ServerMessage {
    public ServerMessage(ServerMessageType serverMessageType) {
        this.serverMessageType = serverMessageType;
    }

    public ServerMessageType getServerMessageType() {
        return serverMessageType;
    }

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    protected ServerMessageType serverMessageType;

    protected String message;
}
