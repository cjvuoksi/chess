package service.socket;

import chess.ChessGame;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.UserCommand;

import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class Resign extends SocketService {
    public Resign(UserCommand command, Session root, Collection<Session> sessions) {
        super(command, root, sessions);
    }

    @Override
    public void run() {
        try {
            Result res = getResult();
            if (res == null) return;

            res.game().game().setGameOver(true);
            ChessGame.TeamColor winner = command.getTeamColor() == WHITE ? BLACK : WHITE;
            res.game().game().setWinner(winner);
            gameDAO.update(res.game());
            sendAll(new Notification(String.format("%s resigned: %s won!", res.auth().username(), winner)));
        } catch (DataAccessException e) {
            sendRoot(new Error(e.getMessage()));
        }
    }
}
