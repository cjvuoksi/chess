package communication;

import request.JoinRequest;
import ui.Observer;
import websocket.commands.Connect;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;

public class WebSocketCommunicator {
    Observer observer;
    WebSocketClient client;
    boolean connected = false;

    public void initialize(Observer observer, JoinRequest request) {
        this.observer = observer;

        try {
            this.client = new WebSocketClient(observer);
            if (this.client.session.isOpen()) {
                this.connected = true;
                client.send(new Connect(request.getGameID(), request.getAuthorization(), request.getPlayerColor()));
            }
        } catch (Exception e) {
            connected = false;
            observer.notify(new Error("Failed to connect to websocket: " + e.getMessage()));
        }
    }

    public void deInit() {
        connected = false;
        if (client != null) {
            client.close();
        }
    }

    public void send(UserGameCommand message) throws Exception {
        client.send(message);
    }
}
