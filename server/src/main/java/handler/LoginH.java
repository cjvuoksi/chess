package handler;

import dataaccess.DataAccessException;
import request.Request;
import request.UserRequest;
import service.Service;
import spark.Response;

public class LoginH extends Handler {
    public LoginH(spark.Request request, Response response, Service service) {
        super(request, response, service);
    }

    @Override
    protected Request getRequest() throws DataAccessException {
        UserRequest req = serializer.fromJson(request.body(), UserRequest.class);
        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            throw new DataAccessException("Error: bad request", 400);
        }
        return req;
    }
}
