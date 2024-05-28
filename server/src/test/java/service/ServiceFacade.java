package service;

import dataaccess.AuthDAO;
import dataaccess.GameDao;
import dataaccess.UserDAO;

public class ServiceFacade extends Service {
    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public GameDao getGameDAO() {
        return gameDAO;
    }
}
