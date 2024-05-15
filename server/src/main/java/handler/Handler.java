package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessException;
import request.Request;
import response.Response;
import service.HTTP.HTTPService;

public abstract class Handler {
    protected final spark.Request request;
    protected final spark.Response response;
    protected final HTTPService service;

    protected final Gson serializer = new GsonBuilder().enableComplexMapKeySerialization().create();

    public Handler(spark.Request request, spark.Response response, HTTPService service) {
        this.request = request;
        this.response = response;
        this.service = service;
    }

    public String run() {
        try {
            response.status(200);
            return serializer.toJson(service.run(getRequest()));
        } catch (DataAccessException e) {
            response.status(e.getStatusCode());
            return serializer.toJson(new Response(e.getMessage()));
        }
    }

    protected abstract Request getRequest() throws DataAccessException;
}
