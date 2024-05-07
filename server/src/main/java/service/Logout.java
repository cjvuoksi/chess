package service;

import dataaccess.DataAccessException;
import model.AuthData;
import request.AuthRequest;
import request.Request;
import response.Response;

public class Logout extends Service {
    @Override
    public Response run(Request req) throws DataAccessException {
        AuthRequest r = (AuthRequest) req;

        AuthData data = authDAO.delete(r.getAuthorization());
        if (data == null) {
            throw new DataAccessException("Error: unauthorized", 401);
        }

        return new Response();
    }
}
