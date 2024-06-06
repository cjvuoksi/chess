package server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class Echo {

    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
    private static final Logger log = LoggerFactory.getLogger(Echo.class);

    @OnWebSocketConnect
    public void connected(Session session) {
        sessions.add(session);
        log.info("Connected");
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        sessions.remove(session);
        log.info("Closed");
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        System.out.println("Got: " + message);   // Print message
        session.getRemote().sendString(message); // and send it back
    }

    @OnWebSocketError
    public void error(Session session, Throwable error) throws IOException {
        error.printStackTrace();
        session.getRemote().sendString("Error");
        session.close();
        sessions.remove(session);
    }
}
