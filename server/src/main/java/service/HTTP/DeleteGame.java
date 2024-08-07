package service.HTTP;

import dataaccess.DataAccessException;
import model.AdminData;
import model.AuthData;
import request.GameRequest;
import request.Request;
import response.Response;

public class DeleteGame extends HTTPService {
    @Override
    public Response run(Request req) throws DataAccessException, ServiceException {
        GameRequest request = (GameRequest) req;

        AuthData auth = authDAO.find(request.getAuthorization());
        if (auth == null) {
            throw new ServiceException("Error: unauthorized", 401);
        }

        AdminData admin = adminDAO.find(auth.username());

        if (admin == null) {
            throw new ServiceException("Error: prohibited", 403);
        }

        if (admin.privileges().games()) {
            gameDAO.delete(request.getGameID());
        }
        return new Response();
    }
}
