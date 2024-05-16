package service.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysql.cj.log.Slf4JLogger;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.Collection;

public class SocketSender {

    private final Gson serializer = new GsonBuilder().enableComplexMapKeySerialization().create();

    private final Slf4JLogger logger = new Slf4JLogger("Sender");

    public void send(ServerMessage message, Session recipient) {
        try {
            debug(message);
            if (recipient.isOpen()) {
                recipient.getRemote().sendString(serializer.toJson(message));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void debug(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> logger.logInfo(((Notification) message).getMessage());
            case ERROR -> logger.logInfo(((Error) message).getMessage());
            case LOAD_GAME -> logger.logInfo(message.getServerMessageType());
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
