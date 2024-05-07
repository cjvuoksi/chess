package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.GameData;
import request.CreateRequest;
import request.Request;
import response.CreateResponse;
import response.Response;

public class Create extends Service {
    static int games = 1;

    @Override
    public Response run(Request req) throws DataAccessException {
        CreateRequest r = (CreateRequest) req;

        if (authDAO.find(r.getAuthorization()) == null) {
            throw new DataAccessException("Error: unauthorized", 401);
        }

        games++;
        gameDAO.create(new GameData(games, null, null, r.getGameName(), new ChessGame()), games);

        return new CreateResponse(games);
    }
}
