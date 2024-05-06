package dataaccess;

import java.util.Collection;
import java.util.HashMap;

public abstract class DAO<V, K> {

    protected HashMap<K, V> db = new HashMap<>();

    public void create(V toAdd, K key) throws DataAccessException {
        V added = db.put(key, toAdd);
    }

    public V find(K query) throws DataAccessException {
        return db.get(query);
    }

    public Collection<V> findAll() throws DataAccessException {
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
