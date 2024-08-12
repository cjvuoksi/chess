package service.HTTP;

import dataaccess.DataAccessException;
import model.AdminData;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.Request;
import request.UpdateRequest;
import response.Response;

public class UpdateUser extends HTTPService {
    @Override
    public Response run(Request req) throws DataAccessException, ServiceException {
        UpdateRequest update = (UpdateRequest) req;

        AuthData auth = authDAO.find(update.getAuthorization());
        if (auth == null) {
            throw new ServiceException("Unauthorized", 401);
        }

        if (update.getPassword() != null && update.getOldPassword() != null) {

            UserData user = userDAO.find(auth.username());
            if (user == null || !BCrypt.checkpw(update.getOldPassword(), user.password())) {
                throw new ServiceException("Error: unauthorized", 401);
            }

            String hash = BCrypt.hashpw(update.getPassword(), BCrypt.gensalt());

            userDAO.delete(user.username()); //TODO update userDAO update

            userDAO.create(new UserData(user.username(), hash, user.email()));
            return new Response();
        } else {
            UserData user = userDAO.find(auth.username());
            if (user == null) {
                throw new ServiceException("Prohibited", 403);
            }
            UserData newUser = userDAO.find(update.getUsername());

            if (newUser == null) {
                userDAO.delete(auth.username());
                userDAO.create(new UserData(update.getUsername(), user.password(), update.getEmail()));
                AdminData admin = adminDAO.find(auth.username());
                if (admin != null) {
                    adminDAO.delete(auth.username());
                    adminDAO.create(new AdminData(update.getUsername(), admin.privileges()));
                    authDAO.update(new AuthData(auth.authToken(), update.getUsername()));
                }
                return new Response();
            } else if (newUser.username().equals(user.username())) {
                userDAO.delete(user.username());
                userDAO.create(new UserData(user.username(), user.password(), update.getEmail()));
                return new Response();
            } else {
                throw new ServiceException("Prohibited", 403);
            }
        }
    }
}
