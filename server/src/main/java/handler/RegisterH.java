package handler;

import request.RegisterRequest;
import request.Request;
import service.HTTP.HTTPService;
import service.HTTP.ServiceException;
import spark.Response;

public class RegisterH extends Handler {
    public RegisterH(spark.Request request, Response response, HTTPService service) {
        super(request, response, service);
    }

    @Override
    protected Request getRequest() throws ServiceException {
        RegisterRequest req = serializer.fromJson(request.body(), RegisterRequest.class);
        if (request.body().isEmpty() || req.getUsername() == null || req.getPassword() == null || req.getEmail() == null) {
            throw new ServiceException("Error: bad request", 400);
        }
        return req;
    }
}
