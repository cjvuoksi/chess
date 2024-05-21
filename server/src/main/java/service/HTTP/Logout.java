package service.HTTP;

import dataaccess.DataAccessException;
import request.AuthRequest;
import request.Request;
import response.Response;

public class Logout extends HTTPService {
    @Override
    public Response run(Request req) throws DataAccessException {
        AuthRequest r = (AuthRequest) req;

        if (r.getAuthorization() == null) {
            throw new DataAccessException("Error: unauthorized", 401);
        }

        int data = authDAO.delete(r.getAuthorization());
        if (data == 0) {
            throw new DataAccessException("Error: unauthorized", 401);
        }

        return new Response();
    }
}
