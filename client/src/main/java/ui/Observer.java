package ui;

import webSocketMessages.serverMessages.ServerMessage;

public interface Observer {
    void notify(ServerMessage message);

    void notifyClosed();
}
