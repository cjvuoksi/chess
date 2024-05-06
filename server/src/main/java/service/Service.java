package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.UserDAO;
import request.Request;
import response.Response;

public abstract class Service {
    protected static UserDAO userDAO = new UserDAO();
    protected static AuthDAO authDAO = new AuthDAO();
    protected static GameDao gameDAO = new GameDao();


    public Service() {
    }

    public abstract Response run(Request req) throws DataAccessException;
}
