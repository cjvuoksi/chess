package dataaccess;

import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO extends DAO<UserData, String> {
    @Override
    protected UserData getResult(ResultSet res) throws SQLException {
        return new UserData(res.getString("username"), res.getString("password"), res.getString("email"));
    }

    @Override
    protected String[] getArgs(UserData data) {
        return new String[]{data.username(), data.password(), data.email()};
    }
}
