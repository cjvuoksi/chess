package dataaccess;

import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAO extends DAO<AuthData, String> {
    @Override
    protected AuthData getResult(ResultSet res) throws SQLException {
        return new AuthData(res.getString("token"), res.getString("username"));
    }

    @Override
    protected String[] getArgs(AuthData data) {
        return new String[]{data.authToken(), data.username()};
    }
}
