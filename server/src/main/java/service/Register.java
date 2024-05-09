package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.RegisterRequest;
import request.Request;
import response.LoginResponse;
import response.Response;

import java.util.UUID;

public class Register extends Service {
    @Override
    public Response run(Request req) throws DataAccessException {
        RegisterRequest r = (RegisterRequest) req;

        if (userDAO.find(r.getUsername()) != null) {
            throw new DataAccessException("Error: already taken", 403);
        }

        String hash = BCrypt.hashpw(r.getPassword(), BCrypt.gensalt());

        userDAO.create(new UserData(r.getUsername(), hash, r.getEmail()));
        String authToken = UUID.randomUUID().toString();
        authDAO.create(new AuthData(authToken, r.getUsername()));

        return new LoginResponse(authToken, r.getUsername());
    }
}
