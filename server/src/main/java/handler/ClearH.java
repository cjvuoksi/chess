package handler;

import dataaccess.DataAccessException;
import request.Request;
import service.HTTP.HTTPService;
import spark.Response;

public class ClearH extends Handler {
    public ClearH(spark.Request request, Response response, HTTPService service) {
        super(request, response, service);
    }

    @Override
    protected Request getRequest() throws DataAccessException {
        return null;
    }
}
