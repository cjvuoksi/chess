package communication;

import com.google.gson.Gson;
import ui.Observer;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserCommand;

import javax.websocket.*;
import java.net.URI;

public class WebSocketClient extends Endpoint {
    private final Observer observer;
    Session session;
    private final Gson gson = new Gson();

    public WebSocketClient(Observer observer) throws Exception {
        URI uri = URI.create("ws://localhost:8080/connect");
        this.observer = observer;
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                observer.notify(gson.fromJson(message, ServerMessage.class));
            }
        });
    }

    private void send(String message) throws Exception {
        if (this.session.isOpen()) {
            this.session.getBasicRemote().sendText(message);
        } else {
            observer.notify(new Error("Connection expired"));
        }
    }

    public void send(UserCommand command) throws Exception {
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
