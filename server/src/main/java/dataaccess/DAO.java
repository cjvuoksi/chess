package dataaccess;

import java.sql.SQLException;
import java.util.Collection;

public abstract class DAO<V, K> {

    static {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "CREATE TABLE IF NOT EXISTS users (\n" +
                    "\tid INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
                    "\tusername VARCHAR(255) NOT NULL,\n" +
                    "\tpwd VARCHAR(255) NOT NULL,\n" +
                    "\temail VARCHAR(255) NOT NULL);\n" +
                    "\t\n" +
                    "CREATE TABLE IF NOT EXISTS auth (\n" +
                    "\tid INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
                    "\ttoken VARCHAR(255) NOT NULL,\n" +
                    "\tusername VARCHAR(255) NOT NULL);\n" +
                    "\n" +
                    "CREATE TABLE IF NOT EXISTS game (\n" +
                    "\tid INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
                    "\tgame JSON NOT NULL,\n" +
                    "\tgame_name VARCHAR(255) NOT NULL,\n" +
                    "\twhite_user VARCHAR(255), \n" +
                    "\tblack_user VARCHAR(255));";

            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected String createStatement;

    public V create(V toAdd, K key) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement(createStatement)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    protected V access(String statement, String... arguments) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }


    protected abstract V create(V toAdd, K key, String statement);

    public V find(K key) throws DataAccessException {
        return db.get(key);
    }

    public Collection<V> findAll() throws DataAccessException {
        return db.values();
    }

    public void update(K key, V newValue) throws DataAccessException {
        db.replace(key, newValue);
    }

    public V delete(K key) throws DataAccessException {
        return db.remove(key);
    }

    public void clear() throws DataAccessException {
        db.clear();
    }
}
