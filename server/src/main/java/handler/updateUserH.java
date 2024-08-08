package handler;

import request.UpdateRequest;
import service.HTTP.HTTPService;
import service.HTTP.ServiceException;
import spark.Request;
import spark.Response;

public class updateUserH extends Handler {
    public updateUserH(Request request, Response response, HTTPService service) {
        super(request, response, service);
    }

    @Override
    protected request.Request getRequest() throws ServiceException {
        UpdateRequest update = serializer.fromJson(request.body(), UpdateRequest.class);

        String token = request.headers("Authorization");
        if (token == null) {
            throw new ServiceException("Unauthorized", 401);
        }

        update.setAuthorization(token);

        return update;
    }
}
