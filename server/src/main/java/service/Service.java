package service;

import dataaccess.DataAccessException;
import request.Request;
import response.Response;

public abstract class Service {

    public Service() {
    }

    public Response run(Request req) throws DataAccessException {
        return new Response();
    }

    public abstract Service create();
}
