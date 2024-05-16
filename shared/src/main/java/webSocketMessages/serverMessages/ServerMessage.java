package webSocketMessages.serverMessages;

import model.GameData;

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

    protected GameData gameData;

    public String getMessage() {
        return message;
    }

    public GameData getGameData() {
        return gameData;
    }
}
