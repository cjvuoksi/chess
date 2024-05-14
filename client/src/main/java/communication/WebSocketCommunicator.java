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
            client.send(new Connect(request.getPlayerColor(), request.getGameID(), request.getAuthorization()));
            connected = true;
        } catch (Exception e) {
            connected = false;
            observer.notify(new Error("Failed to connect to websocket: " + e.getMessage()));
        }
    }
}
