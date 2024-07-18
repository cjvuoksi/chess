package websocket.messages;

import chess.ChessMove;

import java.util.Collection;

public class Moves extends ServerMessage {
    public Moves(ServerMessageType type) {
        super(type);
    }

    public Moves(Collection<ChessMove> moves) {
        super(ServerMessageType.MOVES);
        this.moves = moves;
    }
}
