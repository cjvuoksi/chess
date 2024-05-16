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

public class Leave extends SocketService {
    public Leave(UserCommand command, Session root, Collection<Session> sessions) {
        super(command, root, sessions);
    }

    @Override
    public void run() {
        try {
            AuthData authData = authDAO.find(command.getAuthToken());
            if (authData == null) {
                sendRoot(new Error("Unauthorized"));
                return;
            }
            GameData gameData = gameDAO.find(command.getId());
            if (gameData == null) {
                sendRoot(new Error("Game not found"));
                return;
            }

            if (command.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (!gameData.whiteUsername().equals(authData.username())) {
                    sendRoot(new Error("You are not the white player"));
                    return;
                }

                GameData updated = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
                gameDAO.update(updated);

                sendOthers(new Notification(String.format("%s stopped playing white", authData.username())));
            } else if (command.getTeamColor() == ChessGame.TeamColor.BLACK) {
                if (!gameData.blackUsername().equals(authData.username())) {
                    sendRoot(new Error("You are not the black player"));
                    return;
                }

                GameData updated = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
                gameDAO.update(updated);

                sendOthers(new Notification(String.format("%s stopped playing black", authData.username())));
            } else {
                sendOthers(new Notification(String.format("%s stopped watching", authData.username())));
            }
        } catch (DataAccessException e) {
            sendRoot(new Error(e.getMessage()));
        }
    }
}
