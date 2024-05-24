package handler;

import request.JoinRequest;
import request.Request;
import service.HTTP.HTTPService;
import service.HTTP.ServiceException;
import spark.Response;

public class JoinH extends Handler {
    public JoinH(spark.Request request, Response response, HTTPService service) {
        super(request, response, service);
    }

    @Override
    protected Request getRequest() throws ServiceException {
        JoinRequest req = serializer.fromJson(request.body(), JoinRequest.class);
        if (req.getGameID() == 0 || !request.headers().contains("Authorization")) {
            throw new ServiceException("Error: bad request", 400);
        }
        req.setAuthorization(request.headers("Authorization"));
        return req;
    }
}
