package websocket.commands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMove extends UserGameCommand {

    public MakeMove(Integer id, String authToken, ChessGame.TeamColor teamColor, ChessMove move) {
        super(authToken);
        this.gameID = id;
        this.teamColor = teamColor;
        this.commandType = CommandType.MAKE_MOVE;
    }
}
