package service.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websocket.messages.Error;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;

public class SocketSender {

    private final Gson serializer = new GsonBuilder().enableComplexMapKeySerialization().create();

    private final Logger logger = LoggerFactory.getLogger("Sender");

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
        if (logger.isDebugEnabled()) {
            switch (message.getServerMessageType()) {
                case NOTIFICATION -> logger.debug(((Notification) message).getMessage());
                case ERROR -> logger.debug(((Error) message).getErrorMessage());
                case LOAD_GAME -> logger.debug(String.valueOf(message));
            }
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
