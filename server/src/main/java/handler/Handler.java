package handler;

import dataaccess.DataAccessException;
import request.Request;
import response.Response;
import service.Service;

public abstract class Handler {
    private final spark.Request request;
    private final spark.Response response;
    private final Service service;

    protected Handler(spark.Request request, spark.Response response, Service service) {
        this.request = request;
        this.response = response;
        this.service = service;
    }

    public Response run() {
        return this.run(getRequest());
    }

    protected Response run(Request req) {
        try {
            return service.run(req);
        } catch (DataAccessException e) {
            response.status(e.getStatusCode());
            return new Response(e.getMessage());
        }
    }

    protected abstract Request getRequest();
}
