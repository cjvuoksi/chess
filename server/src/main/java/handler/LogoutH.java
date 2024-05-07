package handler;

import dataaccess.DataAccessException;
import request.AuthRequest;
import request.Request;
import service.Service;
import spark.Response;

public class LogoutH extends Handler {
    public LogoutH(spark.Request request, Response response, Service service) {
        super(request, response, service);
    }

    @Override
    protected Request getRequest() throws DataAccessException {
        return new AuthRequest(request.headers("Authorization"));
    }
}
