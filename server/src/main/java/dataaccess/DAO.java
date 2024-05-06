package dataaccess;

import java.util.Collection;
import java.util.HashMap;

public abstract class DAO<T, K> {

    protected HashMap<K, T> db = new HashMap<>();

    public void create(T toAdd, K key) throws DataAccessException {
        db.put(key, toAdd);
    }

    public T find(K query) throws DataAccessException {
        return db.get(query);
    }

    public Collection<T> findAll() throws DataAccessException {
        return null;
    }

    public void update() throws DataAccessException {

    }

    public void delete() throws DataAccessException {

    }

    public void clear() throws DataAccessException {
        db.clear();
    }
}
