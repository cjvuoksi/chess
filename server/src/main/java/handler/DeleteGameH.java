package handler;

import request.GameRequest;
import service.HTTP.HTTPService;
import service.HTTP.ServiceException;
import spark.Request;
import spark.Response;

public class DeleteGameH extends Handler {

    public DeleteGameH(Request request, Response response, HTTPService service) {
        super(request, response, service);
    }

    @Override
    protected request.Request getRequest() throws ServiceException {
        if (request.body().isEmpty() || !request.headers().contains("Authorization")) {
            throw new ServiceException("Error: bad request", 400);
        }
        GameRequest req = serializer.fromJson(request.body(), GameRequest.class);
        req.setAuthorization(request.headers("Authorization"));
        return req;
    }
}
