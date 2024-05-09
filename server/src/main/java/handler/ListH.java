package handler;

import dataaccess.DataAccessException;
import request.AuthRequest;
import request.Request;
import service.Service;
import spark.Response;

public class ListH extends Handler {
    public ListH(spark.Request request, Response response, Service service) {
        super(request, response, service);
    }

    @Override
    protected Request getRequest() throws DataAccessException {
        if (!request.headers().contains("Authorization")) {
            throw new DataAccessException("Error: unauthorized", 401);
        }

        return new AuthRequest(request.headers("Authorization"));
    }
}