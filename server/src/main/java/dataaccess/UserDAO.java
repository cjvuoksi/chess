package dataaccess;

import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO extends DAO<UserData, String> {

    {
        createStatement = "INSERT INTO users (username, pwd, email) VALUES (?, ?, ?)";
        findStatement = "SELECT * FROM users WHERE username = ?";
        updateStatement = "UPDATE users SET pwd = ?, email = ? WHERE username = ?";
        deleteStatement = "DELETE FROM users WHERE username = ?";
        findAllStatement = "SELECT * FROM users";
        clearStatement = "DELETE FROM users";
    }

    @Override
    protected UserData getResult(ResultSet res) throws SQLException {
        return new UserData(res.getString("username"), res.getString("pwd"), res.getString("email"));
    }

    @Override
    protected String[] getArgs(UserData data) {
        return new String[]{data.username(), data.password(), data.email()};
    }
}
