package service;

import dataaccess.DataAccessException;
import model.GameData;
import request.AuthRequest;
import request.Request;
import response.ListResponse;
import response.Response;

import java.util.Collection;

public class List extends Service {
    @Override
    public Response run(Request req) throws DataAccessException {
        AuthRequest r = (AuthRequest) req;

        if (authDAO.find(r.getAuthorization()) == null) {
            throw new DataAccessException("Error: unauthorized", 401);
        }

        Collection<GameData> games = gameDAO.findAll();

        return new ListResponse(games);
    }
}
