package service.socket;

import chess.ChessGame;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import server.SessionInfo;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

import java.util.Collection;

public class Connect extends SocketService {

    public Connect(UserGameCommand command, Session root, Collection<Session> sessions, SessionInfo sessionInfo) {
        super(command, root, sessions, sessionInfo);
    }

    @Override
    public void run() {
        try {
            Result result = getResult();
            if (result == null) return;

            sendRoot(new LoadGame(result.game()));

            if (result.color() == null) {
                sendOthers(new Notification(result.auth().username() + " started watching the game"));
            } else if (result.color() == ChessGame.TeamColor.WHITE) {
                if (result.auth().username().equals(result.game().whiteUsername())) {
                    sendOthers(new Notification(result.auth().username() + " joined as white"));
                } else {
                    sendRoot(new Error("Error in joining game"));
                }
            } else if (result.color() == ChessGame.TeamColor.BLACK) {
                if (result.auth().username().equals(result.game().blackUsername())) {
                    sendOthers(new Notification(result.auth().username() + " joined as black"));
                } else {
                    sendRoot(new Error("Error in joining game"));
                }
            }
        } catch (DataAccessException e) {
            sendRoot(new Error(e.getMessage()));
        }
    }
}
