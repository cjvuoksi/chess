package service.socket;

import chess.ChessGame;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.UserCommand;

import java.util.Collection;

public class Connect extends SocketService {

    public Connect(UserCommand command, Session root, Collection<Session> sessions) {
        super(command, root, sessions);
    }

    @Override
    public void run() {
        try {
            Result result = getResult();
            if (result == null) return;

            sendAll(new LoadGame(result.game()));

            if (command.getTeamColor() == null) {
                sendOthers(new Notification(result.auth().username() + " started watching the game"));
            } else if (command.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (result.auth().username().equals(result.game().whiteUsername())) {
                    sendOthers(new Notification(result.auth().username() + " joined as white"));
                } else {
                    sendRoot(new Error("Error in joining game"));
                }
            } else if (command.getTeamColor() == ChessGame.TeamColor.BLACK) {
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
