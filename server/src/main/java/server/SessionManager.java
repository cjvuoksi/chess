package server;

import org.eclipse.jetty.websocket.api.Session;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SessionManager {
    private final Map<Integer, Collection<Session>> sessions = new HashMap<>();

    private final Map<Session, Integer> sessionMap = new HashMap<>();

    public SessionManager() {
    }

    public synchronized void add(Session session, int gameID) {
        Collection<Session> gameSessions = sessions.getOrDefault(gameID, new HashSet<>());
        gameSessions.add(session);
        sessions.put(gameID, gameSessions);
        sessionMap.put(session, gameID);
    }

    public synchronized void remove(Session session, int gameID) {
        Collection<Session> gameSessions = sessions.get(gameID);
        gameSessions.remove(session);
        if (gameSessions.isEmpty()) {
            sessions.remove(gameID);
        } else {
            sessions.put(gameID, gameSessions);
        }

        if (session.isOpen()) {
            session.close();
        }

        sessionMap.remove(session);
    }

    public synchronized Collection<Session> get(int gameID) {
        return sessions.get(gameID);
    }

    public synchronized Integer get(Session session) {
        return sessionMap.get(session);
    }
}
