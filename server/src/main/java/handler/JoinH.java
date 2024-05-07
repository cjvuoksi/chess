package handler;

import dataaccess.DataAccessException;
import request.JoinRequest;
import request.Request;
import service.Service;
import spark.Response;

public class JoinH extends Handler {
    public JoinH(spark.Request request, Response response, Service service) {
        super(request, response, service);
    }

    @Override
    protected Request getRequest() throws DataAccessException {
        JoinRequest req = serializer.fromJson(request.body(), JoinRequest.class);
        if (req.getGameID() == 0 || !request.headers().contains("Authorization")) {
            throw new DataAccessException("Error: bad request", 400);
        }
        req.setAuthorization(request.headers("Authorization"));
        return req;
    }
}
