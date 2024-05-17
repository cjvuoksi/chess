package ui;


import websocket.messages.ServerMessage;

public interface Observer {
    void notify(ServerMessage message);

    void notifyClosed();
}
