package communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ui.Observer;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

import static port.Port.port;

public class WebSocketClient extends Endpoint {
    private final Observer observer;
    Session session;
    private final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

    public WebSocketClient(Observer observer) throws Exception {
        URI uri = URI.create("ws://localhost:" + port + "/ws");
        this.observer = observer;
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        //Must be anonymous class; Lambda fails
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                observer.notify(gson.fromJson(message, ServerMessage.class));
            }
        });
    }

    public void restart() throws Exception {
        URI uri = URI.create("ws://localhost:" + port + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.session.addMessageHandler((MessageHandler.Whole<String>) (message) -> {
            observer.notify(gson.fromJson(message, ServerMessage.class));
        });
    }

    private void send(String message) throws Exception {
        if (this.session.isOpen()) {
            this.session.getBasicRemote().sendText(message);
        } else {
            observer.notify(new Error("Connection expired"));
        }
    }

    public void send(UserGameCommand command) throws Exception {
        String message = gson.toJson(command);
        send(message);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        if (!closeReason.getReasonPhrase().isEmpty()) {
            observer.notify(new Error("Connection closed: " + closeReason.getReasonPhrase().replaceAll("\"", "")));
            observer.notify(new Notification("Press enter to exit"));
        }
        observer.notifyClosed();
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void close() {
        if (session.isOpen()) {
            try {
                session.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
