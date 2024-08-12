package service.HTTP;

import dataaccess.DataAccessException;
import model.AuthData;
import request.AuthRequest;
import request.Request;
import response.LoginResponse;
import response.Response;

public class Authenticate extends HTTPService {
    @Override
    public Response run(Request req) throws DataAccessException, ServiceException {
        AuthRequest authRequest = (AuthRequest) req;

        AuthData authData = authDAO.find(authRequest.getAuthorization());

        if (authData == null) {
            throw new ServiceException("Unauthorized", 401);
        }

        return new LoginResponse(authData.authToken(), authData.username());
    }
}
