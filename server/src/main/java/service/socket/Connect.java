package service.socket;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.Error;
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
            AuthData auth = authDAO.find(command.getAuthToken());

            if (auth == null) {
                sendRoot(new Error("Unauthorized"));
                return;
            }

            GameData game = gameDAO.find(command.getId());

            if (game == null) {
                sendRoot(new Error("Game not found"));
                return;
            }

            if (command.getTeamColor() == null) {
                sendOthers(new Notification(auth.username() + " started watching the game"));
            } else if (command.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (auth.username().equals(game.whiteUsername())) {
                    sendOthers(new Notification(auth.username() + " joined as white"));
                } else {
                    sendRoot(new Error("Error in joining game"));
                }
            } else if (command.getTeamColor() == ChessGame.TeamColor.BLACK) {
                if (auth.username().equals(game.blackUsername())) {
                    sendOthers(new Notification(auth.username() + " joined as black"));
                } else {
                    sendRoot(new Error("Error in joining game"));
                }
            }


        } catch (DataAccessException e) {
            sendRoot(new Error(e.getMessage()));
        }
    }
}
