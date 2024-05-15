package service.socket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.Collection;

public class SocketSender {

    private final Gson serializer = new Gson();

    public void send(ServerMessage message, Session recipient) {
        try {
            if (recipient.isOpen()) {
                recipient.getRemote().sendString(serializer.toJson(message));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(ServerMessage message, Collection<Session> recipients) {
        recipients.forEach(session -> send(message, session));
    }

    public void send(ServerMessage message, Session exclude, Collection<Session> recipients) {
        if (null == recipients) {
            return;
        }

        recipients.forEach(session -> {
            if (!session.equals(exclude)) {
                send(message, session);
            }
        });
    }
}
