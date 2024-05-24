package service.HTTP;

import dataaccess.DataAccessException;
import model.GameData;
import request.AuthRequest;
import request.Request;
import response.ListResponse;
import response.Response;

import java.util.Collection;

public class List extends HTTPService {
    @Override
    public Response run(Request req) throws DataAccessException, ServiceException {
        AuthRequest r = (AuthRequest) req;

        if (r.getAuthorization() == null || authDAO.find(r.getAuthorization()) == null) {
            throw new ServiceException("Error: unauthorized", 401);
        }

        Collection<GameData> games = gameDAO.findAll();

        return new ListResponse(games);
    }
}
