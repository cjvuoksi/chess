package request;

public class GameRequest extends AuthRequest {
    private final int gameID;

    public GameRequest(String authorization, int gameID) {
        super(authorization);
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
