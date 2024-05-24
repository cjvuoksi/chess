package handler;

import request.CreateRequest;
import request.Request;
import service.HTTP.HTTPService;
import service.HTTP.ServiceException;
import spark.Response;

public class CreateH extends Handler {
    public CreateH(spark.Request request, Response response, HTTPService service) {
        super(request, response, service);
    }

    @Override
    protected Request getRequest() throws ServiceException {
        if (request.body().isEmpty() || !request.headers().contains("Authorization")) {
            throw new ServiceException("Error: bad request", 400);
        }
        CreateRequest req = serializer.fromJson(request.body(), CreateRequest.class);
        req.setAuthorization(request.headers("Authorization"));
        return req;
    }
}
