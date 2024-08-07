package service.HTTP;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.GameData;
import request.CreateRequest;
import request.Request;
import response.CreateResponse;
import response.Response;
import service.Service;

public class Create extends HTTPService {

    @Override
    public Response run(Request req) throws ServiceException, DataAccessException {
        CreateRequest r = (CreateRequest) req;

        if (r.getAuthorization() == null || r.getGameName() == null) {
            throw new ServiceException("Error: bad request", 400);
        }

        if (Service.authDAO.find(r.getAuthorization()) == null) {
            throw new ServiceException("Error: unauthorized", 401);
        }
        Service.gameDAO.setMake(true);
        int gameID = Service.gameDAO.create(new GameData(0, null, null, r.getGameName(), new ChessGame()));

        return new CreateResponse(gameID);
    }
}
