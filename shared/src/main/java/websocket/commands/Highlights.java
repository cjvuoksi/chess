package websocket.commands;

import chess.ChessMove;

public class Highlights extends UserGameCommand {
    public Highlights(String authToken) {
        super(authToken);
        commandType = CommandType.HIGHLIGHT;
    }

    public Highlights(String authToken, ChessMove move, Integer gameID) {
        this(authToken);
        this.move = move;
        this.gameID = gameID;
    }
}