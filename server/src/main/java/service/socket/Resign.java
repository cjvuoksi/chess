package service.socket;

import chess.ChessGame;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import server.SessionInfo;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.messages.Notification;

import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class Resign extends SocketService {
    public Resign(UserGameCommand command, Session root, Collection<Session> sessions, SessionInfo sessionInfo) {
        super(command, root, sessions, sessionInfo);
    }

    @Override
    public void run() {
        try {
            Result result = getResult();
            if (result == null) return;

            if (result.color() == null) {
                sendRoot(new Error("Observer cannot resign"));
                return;
            }

            if (result.game().game().isGameOver()) {
                sendRoot(new Error("Game is already over"));
                return;
            }

            result.game().game().setGameOver(true);
            ChessGame.TeamColor winner = result.color() == WHITE ? BLACK : WHITE;
            result.game().game().setWinner(winner);
            gameDAO.update(result.game());
            sendAll(new Notification(String.format("%s resigned: %s won!", result.auth().username(), winner)));
        } catch (DataAccessException e) {
            sendRoot(new Error(e.getMessage()));
        }
    }
}
