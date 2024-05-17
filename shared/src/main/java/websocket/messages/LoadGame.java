package websocket.messages;

import model.GameData;

public class LoadGame extends ServerMessage {
    public LoadGame(GameData gameData) {
        super(ServerMessageType.LOAD_GAME);
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.game = gameData;
    }
}
