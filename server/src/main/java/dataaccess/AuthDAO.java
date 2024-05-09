package dataaccess;

import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAO extends DAO<AuthData, String> {
    {
        createStatement = "INSERT INTO auth (token, username) VALUES (?, ?)";
        findStatement = "SELECT * FROM auth WHERE token = ?";
        updateStatement = "UPDATE auth SET username = ? WHERE token = ?";
        deleteStatement = "DELETE FROM auth WHERE token = ?";
        findallStatement = "SELECT * FROM auth";
        clearStatement = "DELETE FROM auth";
    }



    @Override
    protected AuthData getResult(ResultSet res) throws SQLException {
        return new AuthData(res.getString("token"), res.getString("username"));
    }

    @Override
    protected String[] getArgs(AuthData data) {
        return new String[]{data.authToken(), data.username()};
    }
}
