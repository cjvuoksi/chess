package websocket.commands;

import chess.ChessGame;

public class Connect extends UserGameCommand {

    public Connect(Integer id, String authToken, ChessGame.TeamColor teamColor) {
        super(authToken);
        this.gameID = id;
        this.teamColor = teamColor;
        this.commandType = CommandType.CONNECT;
    }
}
