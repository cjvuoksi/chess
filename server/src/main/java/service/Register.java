package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import request.Request;
import response.LoginResponse;
import response.Response;

import java.util.UUID;

public class Register extends Service {
    @Override
    public Response run(Request req) throws DataAccessException {
        RegisterRequest r = (RegisterRequest) req;

        userDAO.create(new UserData(r.getUsername(), r.getPassword(), r.getEmail()), r.getUsername());
        String authToken = UUID.randomUUID().toString();
        authDAO.create(new AuthData(authToken, r.getUsername()), authToken);

        return new LoginResponse(authToken, r.getUsername());
    }
}
