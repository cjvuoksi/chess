package service.HTTP;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.Request;
import request.UserRequest;
import response.LoginResponse;
import response.Response;

import java.util.UUID;

public class Login extends HTTPService {
    @Override
    public Response run(Request req) throws DataAccessException {
        UserRequest r = (UserRequest) req;

        if (r.getUsername() == null || r.getPassword() == null) {
            throw new DataAccessException("Error: bad request", 400);
        }

        UserData user = userDAO.find(r.getUsername());
        if (user == null || !BCrypt.checkpw(r.getPassword(), user.password())) {
            throw new DataAccessException("Error: unauthorized", 401);
        }

        String authToken = UUID.randomUUID().toString();
        authDAO.create(new AuthData(authToken, r.getUsername()));
        return new LoginResponse(authToken, r.getUsername());
    }
}
