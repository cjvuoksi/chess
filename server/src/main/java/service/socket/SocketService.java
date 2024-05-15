package service.socket;

import org.eclipse.jetty.websocket.api.Session;
import service.Service;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserCommand;

import java.util.Collection;

public abstract class SocketService extends Service {
    private final SocketSender sender = new SocketSender();
    protected final UserCommand command;
    private final Session root;
    private final Collection<Session> sessions;

    public SocketService(UserCommand command, Session root, Collection<Session> sessions) {
        this.command = command;
        this.root = root;
        this.sessions = sessions;
    }

    public abstract void run();

    public void sendRoot(ServerMessage message) {
        sender.send(message, root);
    }

    public void sendOthers(ServerMessage message) {
        sender.send(message, root, sessions);
    }

    public void sendAll(ServerMessage message) {
        sender.send(message, sessions);
    }
}
