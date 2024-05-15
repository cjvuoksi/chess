package webSocketMessages.userCommands;

import chess.ChessGame;

public class Connect extends UserCommand {

    public Connect(Integer id, String authToken, ChessGame.TeamColor teamColor) {
        super(id, authToken, teamColor);
    }
}
