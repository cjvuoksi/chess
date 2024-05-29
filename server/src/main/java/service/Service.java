package service;

import dataaccess.AuthDAO;
import dataaccess.GameDao;
import dataaccess.UserDAO;

public abstract class Service {
    protected static final UserDAO userDAO = new UserDAO();
    protected static final AuthDAO authDAO = new AuthDAO();
    protected static final GameDao gameDAO = new GameDao();
}
