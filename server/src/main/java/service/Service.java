package service;

import dataaccess.AdminDAO;
import dataaccess.AuthDAO;
import dataaccess.GameDao;
import dataaccess.UserDAO;

public abstract class Service {
    protected static UserDAO userDAO = new UserDAO();
    protected static AuthDAO authDAO = new AuthDAO();
    protected static GameDao gameDAO = new GameDao();
    protected static AdminDAO adminDAO = new AdminDAO();
}
