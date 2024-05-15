package webSocketMessages.userCommands;

import chess.ChessGame;

public class Resign extends UserCommand {

    public Resign(Integer id, String authToken, ChessGame.TeamColor teamColor) {
        super(id, authToken, teamColor);
        this.commandType = CommandType.RESIGN;
    }
}
