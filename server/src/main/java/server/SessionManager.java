package server;

import com.mysql.cj.log.Slf4JLogger;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SessionManager {
    private final Map<Integer, Collection<Session>> sessions = new HashMap<>();

    private final Map<Session, SessionInfo> sessionMap = new HashMap<>();

    private final Slf4JLogger logger = new Slf4JLogger("SessionManager");

    public SessionManager() {
    }

    public synchronized void add(Session session, SessionInfo info) {
        Collection<Session> gameSessions = sessions.getOrDefault(info.id(), new HashSet<>());
        gameSessions.add(session);
        sessions.put(info.id(), gameSessions);
        sessionMap.put(session, info);
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

    public synchronized SessionInfo get(Session session) {
        return sessionMap.get(session);
    }
}
