package handler;

import request.Request;
import request.UserRequest;
import service.HTTP.HTTPService;
import service.HTTP.ServiceException;
import spark.Response;

public class LoginH extends Handler {
    public LoginH(spark.Request request, Response response, HTTPService service) {
        super(request, response, service);
    }

    @Override
    protected Request getRequest() throws ServiceException {
        UserRequest req = serializer.fromJson(request.body(), UserRequest.class);
        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            throw new ServiceException("Error: bad request", 400);
        }
        return req;
    }
}
