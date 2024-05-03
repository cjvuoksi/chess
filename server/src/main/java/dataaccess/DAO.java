package dataaccess;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public abstract class DAO<T,K> {

    protected HashMap<K,T> db = new HashMap<>();

    void create() throws DataAccessException {

    }

    T find(K query) throws DataAccessException {
        return db.get(query);
    }

    Collection<T> findAll() throws DataAccessException {
        return null;
    }

    void update() throws DataAccessException {

    }

    void delete() throws DataAccessException {

    }
}
