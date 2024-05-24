package handler;

import request.AuthRequest;
import request.Request;
import service.HTTP.HTTPService;
import service.HTTP.ServiceException;
import spark.Response;

public class ListH extends Handler {
    public ListH(spark.Request request, Response response, HTTPService service) {
        super(request, response, service);
    }

    @Override
    protected Request getRequest() throws ServiceException {
        if (!request.headers().contains("Authorization")) {
            throw new ServiceException("Error: unauthorized", 401);
        }

        return new AuthRequest(request.headers("Authorization"));
    }
}
