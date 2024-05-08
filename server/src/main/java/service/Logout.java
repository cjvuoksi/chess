package service;

import dataaccess.DataAccessException;
import request.AuthRequest;
import request.Request;
import response.Response;

public class Logout extends Service {
    @Override
    public Response run(Request req) throws DataAccessException {
        AuthRequest r = (AuthRequest) req;

        int data = authDAO.delete(r.getAuthorization());
        if (data == 0) {
            throw new DataAccessException("Error: unauthorized", 401);
        }

        return new Response();
    }
}
