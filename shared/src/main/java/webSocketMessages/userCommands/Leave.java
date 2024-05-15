package webSocketMessages.userCommands;

import chess.ChessGame;

public class Leave extends UserCommand {

    public Leave(Integer id, String authToken, ChessGame.TeamColor teamColor) {
        super(id, authToken, teamColor);
        this.commandType = CommandType.LEAVE;
    }
}
