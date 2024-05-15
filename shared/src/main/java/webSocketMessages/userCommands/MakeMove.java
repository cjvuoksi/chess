package webSocketMessages.userCommands;

import chess.ChessGame;

public class MakeMove extends UserCommand {

    public MakeMove(Integer id, String authToken, ChessGame.TeamColor teamColor) {
        super(id, authToken, teamColor);
        this.commandType = CommandType.MAKE_MOVE;
    }
}
