package response;

public class CreateResponse extends Response {
    private final int gameID;

    public CreateResponse(int gameID) {
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}
