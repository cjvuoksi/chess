package websocket.commands;

import chess.ChessGame;

public class Resign extends UserGameCommand {

    public Resign(Integer id, String authToken, ChessGame.TeamColor teamColor) {
        super(authToken);
        this.gameID = id;
        this.teamColor = teamColor;
        this.commandType = CommandType.RESIGN;
    }
}
