package response;

import model.GameData;

import java.util.Collection;

public class ListResponse extends Response {
    private final Collection<GameData> games;

    public ListResponse(Collection<GameData> games) {
        this.games = games;
    }
}
