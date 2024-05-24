package service.HTTP;

import dataaccess.DataAccessException;
import request.Request;
import response.Response;
import service.Service;

public abstract class HTTPService extends Service {

    public HTTPService() {
    }

    public abstract Response run(Request req) throws DataAccessException, ServiceException;
}
