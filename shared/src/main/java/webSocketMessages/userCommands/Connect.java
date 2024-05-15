package webSocketMessages.userCommands;

import chess.ChessGame;

public class Connect extends UserCommand {
    private final ChessGame.TeamColor teamColor;
    private final int gameID;
    private final String auth;

    public Connect(ChessGame.TeamColor teamColor, int gameID, String auth) {
        super();
        this.teamColor = teamColor;
        this.gameID = gameID;
        this.auth = auth;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    public int getGameID() {
        return gameID;
    }

    public String getAuth() {
        return auth;
    }
}
