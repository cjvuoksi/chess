package dataaccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashSet;

public abstract class DAO<V extends Record, K> {

    static {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                    "username VARCHAR(255) NOT NULL," +
                    "pwd VARCHAR(255) NOT NULL," +
                    "email VARCHAR(255) NOT NULL);" +
                    "CREATE TABLE IF NOT EXISTS auth (" +
                    "id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                    "token VARCHAR(255) NOT NULL," +
                    "username VARCHAR(255) NOT NULL);" +
                    "CREATE TABLE IF NOT EXISTS game (" +
                    "id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                    "game JSON NOT NULL," +
                    "game_name VARCHAR(255) NOT NULL," +
                    "white_user VARCHAR(255), " +
                    "black_user VARCHAR(255));";

            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected Collection<V> access(String statement, String... arguments) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                setPreparedStatement(arguments, preparedStatement);
                var res = preparedStatement.executeQuery();
                Collection<V> results = new HashSet<>();
                while (res.next()) {
                    results.add(getResult(res));
                }
                return results;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    protected int update(Boolean returnKey, String statement, String... arguments) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                setPreparedStatement(arguments, preparedStatement);
                int updatedRows = preparedStatement.executeUpdate();
                if (!returnKey) {
                    return updatedRows;
                }
                try (var keys = preparedStatement.getGeneratedKeys()) {
                    keys.next();
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

    }

    protected abstract V getResult(ResultSet res) throws SQLException;

    protected abstract String[] getArgs(V data);

    protected String createStatement;
    protected String findStatement;
    protected String updateStatement;
    protected String deleteStatement;
    protected String findallStatement;
    protected String clearStatement;

    public int create(V toAdd, K key) throws DataAccessException {
        return update(true, createStatement, getArgs(toAdd));
    }

    ;

    public V find(K key) throws DataAccessException {
        Collection<V> results = access(findStatement, key.toString());
        if (results.isEmpty()) {
            return null;
        }
        return results.iterator().next();
    }

    public Collection<V> findAll() throws DataAccessException {
        return access(findallStatement);
    }

    public void update(V newValue) throws DataAccessException {
        update(false, updateStatement, getArgs(newValue));
    }

    public int delete(K key) throws DataAccessException {
        return update(false, deleteStatement, key.toString());
    }

    public void clear() throws DataAccessException {
        return update(false, clearStatement)
    }

    private static void setPreparedStatement(String[] arguments, PreparedStatement preparedStatement) throws SQLException {
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] != null) {
                preparedStatement.setString(i + 1, arguments[i]);
            } else {
                preparedStatement.setNull(1 + i, Types.VARCHAR);
            }
        }
    }
}
