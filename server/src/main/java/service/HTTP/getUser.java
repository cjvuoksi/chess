package service.HTTP;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import request.AuthRequest;
import request.Request;
import response.Response;
import response.UserResponse;

public class getUser extends HTTPService {
    @Override
    public Response run(Request req) throws DataAccessException, ServiceException {
        AuthRequest request = (AuthRequest) req;

        AuthData auth = authDAO.find(request.getAuthorization());
        if (auth == null) {
            throw new ServiceException("Unauthorized", 401);
        }

        UserData user = userDAO.find(auth.username());
        if (user == null) {
            throw new ServiceException("User not found", 404);
        }

        return new UserResponse(user.username(), user.email());
    }
}
