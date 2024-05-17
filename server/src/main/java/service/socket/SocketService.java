package service.socket;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.SessionInfo;
import service.Service;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.messages.ServerMessage;

import java.util.Collection;

public abstract class SocketService extends Service {
    private final SocketSender sender = new SocketSender();
    protected final UserGameCommand command;
    protected final Session root;
    private final Collection<Session> sessions;
    protected final SessionInfo info;

    public SocketService(UserGameCommand command, Session root, Collection<Session> sessions, SessionInfo info) {
        this.command = command;
        this.root = root;
        this.sessions = sessions;
        this.info = info;
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

    protected Result getResult() throws DataAccessException {
        AuthData auth = authDAO.find(command.getAuthString());

        if (auth == null) {
            sendRoot(new Error("Unauthorized"));
            return null;
        }

        GameData game = gameDAO.find(command.getGameID());

        if (game == null) {
            sendRoot(new Error("Game not found"));
            return null;
        }

        ChessGame.TeamColor color = null;

        if (auth.username().equals(game.whiteUsername())) {
            color = ChessGame.TeamColor.WHITE;
        } else if (auth.username().equals(game.blackUsername())) {
            color = ChessGame.TeamColor.BLACK;
        }


        return new Result(auth, game, color);
    }

    protected record Result(AuthData auth, GameData game, ChessGame.TeamColor color) {
    }
}
