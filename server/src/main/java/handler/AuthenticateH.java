package handler;

import request.AuthRequest;
import service.HTTP.HTTPService;
import service.HTTP.ServiceException;
import spark.Request;
import spark.Response;

public class AuthenticateH extends Handler {
    public AuthenticateH(Request request, Response response, HTTPService service) {
        super(request, response, service);
    }

    @Override
    protected request.Request getRequest() throws ServiceException {
        String token = request.headers("Authorization");

        if (token == null) {
            throw new ServiceException("Bad request", 400);
        }

        return new AuthRequest(token);
    }
}
