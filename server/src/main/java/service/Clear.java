package service;

import dataaccess.DataAccessException;
import request.Request;
import response.Response;

public class Clear extends Service {

    @Override
    public Response run(Request req) throws DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
        return new Response();
    }
}
