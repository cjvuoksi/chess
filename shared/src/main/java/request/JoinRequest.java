package request;

import chess.ChessGame;

public class JoinRequest extends GameRequest {

    private final ChessGame.TeamColor playerColor;

    public JoinRequest(String authorization, ChessGame.TeamColor playerColor, int gameID) {
        super(authorization, gameID);
        this.playerColor = playerColor;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

}
