package response;

import model.GameData;

import java.util.Vector;

public class ListResponse extends Response {
    private final Vector<GameData> games;

    public ListResponse(Vector<GameData> games) {
        this.games = games;
    }
}
