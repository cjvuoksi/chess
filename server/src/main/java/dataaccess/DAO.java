package dataaccess;

import java.util.Collection;
import java.util.HashMap;

public abstract class DAO<V, K> {

    protected HashMap<K, V> db = new HashMap<>();

    public void create(V toAdd, K key) throws DataAccessException {
        V added = db.put(key, toAdd);
    }

    public V find(K key) throws DataAccessException {
        return db.get(key);
    }

    public Collection<V> findAll() throws DataAccessException {
        return null;
    }

    public void update() throws DataAccessException {

    }

    public V delete(K key) throws DataAccessException {
        return db.remove(key);
    }

    public void clear() throws DataAccessException {
        db.clear();
    }
}
