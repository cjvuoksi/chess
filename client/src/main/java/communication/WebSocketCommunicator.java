package communication;

import request.JoinRequest;
import ui.Observer;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.userCommands.Connect;

public class WebSocketCommunicator {
    Observer observer;
    WebSocketClient client;
    boolean connected = false;

    public void initialize(Observer observer, JoinRequest request) {
        this.observer = observer;

        try {
            this.client = new WebSocketClient(observer);
            client.send(new Connect(request.getGameID(), request.getAuthorization(), request.getPlayerColor()));
            connected = true;
        } catch (Exception e) {
            connected = false;
            observer.notify(new Error("Failed to connect to websocket: " + e.getMessage()));
        }
    }

    public void deinit() {
        connected = false;
        if (client != null) {
            client.close();
        }
    }
}
