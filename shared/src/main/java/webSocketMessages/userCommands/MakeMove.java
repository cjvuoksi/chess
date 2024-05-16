package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMove extends UserCommand {

    private final ChessMove move;

    public MakeMove(Integer id, String authToken, ChessGame.TeamColor teamColor, ChessMove move) {
        super(id, authToken, teamColor);
        this.move = move;
        this.commandType = CommandType.MAKE_MOVE;
    }

    public ChessMove getMove() {
        return move;
    }
}
