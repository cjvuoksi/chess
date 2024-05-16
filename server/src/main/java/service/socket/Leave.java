package service.socket;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.UserCommand;

import java.util.Collection;

public class Leave extends SocketService {
    public Leave(UserCommand command, Session root, Collection<Session> sessions) {
        super(command, root, sessions);
    }

    @Override
    public void run() {
        try {
            Result result = getResult();
            if (result == null) return;

            if (command.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (!result.game().whiteUsername().equals(result.auth().username())) {
                    sendRoot(new Error("You are not the white player"));
                    return;
                }

                GameData updated = new GameData(result.game().gameID(), null, result.game().blackUsername(), result.game().gameName(), result.game().game());
                gameDAO.update(updated);

                sendOthers(new Notification(String.format("%s stopped playing white", result.auth().username())));
            } else if (command.getTeamColor() == ChessGame.TeamColor.BLACK) {
                if (!result.game().blackUsername().equals(result.auth().username())) {
                    sendRoot(new Error("You are not the black player"));
                    return;
                }

                GameData updated = new GameData(result.game().gameID(), result.game().whiteUsername(), null, result.game().gameName(), result.game().game());
                gameDAO.update(updated);

                sendOthers(new Notification(String.format("%s stopped playing black", result.auth().username())));
            } else {
                sendOthers(new Notification(String.format("%s stopped watching", result.auth().username())));
            }
        } catch (DataAccessException e) {
            sendRoot(new Error(e.getMessage()));
        }
    }
}
