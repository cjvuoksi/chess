package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMove extends UserCommand {

    public MakeMove(Integer id, String authToken, ChessGame.TeamColor teamColor, ChessMove move) {
        super(id, authToken, teamColor);
        this.move = move;
        this.commandType = CommandType.MAKE_MOVE;
    }
}
