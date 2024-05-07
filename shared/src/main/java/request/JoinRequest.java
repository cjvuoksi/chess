package request;

import chess.ChessGame;

public class JoinRequest extends AuthRequest {

    private final ChessGame.TeamColor playerColor;
    private final int gameID;

    protected JoinRequest(String authorization, ChessGame.TeamColor playerColor, int gameID) {
        super(authorization);
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public int getGameID() {
        return gameID;
    }
}
