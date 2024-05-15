package webSocketMessages.userCommands;

import chess.ChessGame;

public class Connect extends UserCommand {
    private final ChessGame.TeamColor teamColor;

    public Connect(ChessGame.TeamColor teamColor, int gameID, String auth) {
        super(gameID, auth);
        this.teamColor = teamColor;

    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }
}
