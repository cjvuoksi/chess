package webSocketMessages.serverMessages;

import model.GameData;

public class LoadGame extends ServerMessage {
    public LoadGame(GameData gameData) {
        super(ServerMessageType.LOAD_GAME);
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.gameData = gameData;
    }
}
