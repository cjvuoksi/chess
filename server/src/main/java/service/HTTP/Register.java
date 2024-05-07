package service.HTTP;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.RegisterRequest;
import request.Request;
import response.LoginResponse;
import response.Response;

import java.util.UUID;

public class Register extends HTTPService {
    @Override
    public Response run(Request req) throws DataAccessException {
        RegisterRequest r = (RegisterRequest) req;

        if (r.getUsername() == null || r.getPassword() == null || r.getEmail() == null) {
            throw new DataAccessException("Error: bad request", 400);
        }

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
