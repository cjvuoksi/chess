package websocket.commands;

import chess.ChessGame;

public class Leave extends UserGameCommand {

    public Leave(Integer id, String authToken, ChessGame.TeamColor teamColor) {
        super(authToken);
        this.gameID = id;
        this.teamColor = teamColor;
        this.commandType = CommandType.LEAVE;
    }
}
