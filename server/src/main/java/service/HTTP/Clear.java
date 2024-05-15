package service.HTTP;

import dataaccess.DataAccessException;
import request.Request;
import response.Response;
import service.Service;

public class Clear extends HTTPService {

    @Override
    public Response run(Request req) throws DataAccessException {
        Service.userDAO.clear();
        Service.gameDAO.clear();
        Service.authDAO.clear();
        return new Response();
    }
}
