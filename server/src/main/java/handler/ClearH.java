package handler;

import dataaccess.DataAccessException;
import request.Request;
import service.Service;
import spark.Response;

public class ClearH extends Handler {
    public ClearH(spark.Request request, Response response, Service service) {
        super(request, response, service);
    }

    @Override
    protected Request getRequest() throws DataAccessException {
        return null;
    }
}
