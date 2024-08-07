package dataaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AdminData;
import model.Privileges;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO extends DAO<AdminData, String> {
    private final Gson serializer = new GsonBuilder().enableComplexMapKeySerialization().create();

    {
        createStatement = "INSERT INTO su (su_username, permissions) VALUES (?, ?)";
        findStatement = "SELECT * FROM su WHERE su_username = ?";
        updateStatement = "UPDATE su SET privileges = ? WHERE su_username = ?";
        deleteStatement = "DELETE FROM su WHERE su_username = ?";
        findAllStatement = "SELECT * FROM su";
        clearStatement = "DELETE FROM su";
    }

    @Override
    protected AdminData getResult(ResultSet res) throws SQLException {
        return new AdminData(
                res.getString("su_username"),
                serializer.fromJson(res.getString("permissions"), Privileges.class));
    }

    @Override
    protected String[] getArgs(AdminData data) {
        return new String[]{data.username(), serializer.toJson(data.privileges())};
    }

}
