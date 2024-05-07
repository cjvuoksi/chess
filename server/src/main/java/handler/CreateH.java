package handler;

import dataaccess.DataAccessException;
import request.CreateRequest;
import request.Request;
import service.Service;
import spark.Response;

public class CreateH extends Handler {
    public CreateH(spark.Request request, Response response, Service service) {
        super(request, response, service);
    }

    @Override
    protected Request getRequest() throws DataAccessException {
        if (request.body().isEmpty() || !request.headers().contains("Authorization")) {
            throw new DataAccessException("Error: bad request", 400);
        }
        CreateRequest req = serializer.fromJson(request.body(), CreateRequest.class);
        req.setAuthorization(request.headers("Authorization"));
        return req;
    }
}
