package websocket.messages;

import chess.ChessMove;
import model.GameData;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION,
        MOVES
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ServerMessage that))
            return false;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }

    protected String message;

    protected String errorMessage;

    protected GameData game;

    protected Collection<ChessMove> moves;

    public String getMessage() {
        return message;
    }

    public GameData getGameData() {
        return game;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Collection<ChessMove> getMoves() {
        return moves;
    }
}
