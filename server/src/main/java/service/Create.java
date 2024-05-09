package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.GameData;
import request.CreateRequest;
import request.Request;
import response.CreateResponse;
import response.Response;

public class Create extends Service {

    @Override
    public Response run(Request req) throws DataAccessException {
        CreateRequest r = (CreateRequest) req;

        if (authDAO.find(r.getAuthorization()) == null) {
            throw new DataAccessException("Error: unauthorized", 401);
        }

        int gameID = gameDAO.create(new GameData(0, null, null, r.getGameName(), new ChessGame()));

        return new CreateResponse(gameID);
    }
}
