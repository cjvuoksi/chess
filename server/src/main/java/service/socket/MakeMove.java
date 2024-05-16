package service.socket;

import chess.ChessGame;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.UserCommand;

import java.util.Collection;

public class MakeMove extends SocketService {
    public MakeMove(UserCommand command, Session root, Collection<Session> sessions) {
        super(command, root, sessions);
    }

    @Override
    public void run() {
        try {

            Result result = getResult();
            if (result == null) return;
            GameData gameData = result.game();

            if (command.getTeamColor() != gameData.game().getTeamTurn()) {
                sendRoot(new Error("Not your turn"));
            } else {
                gameData.game().makeMove(command.getMove());

                ChessGame.TeamColor opponent = gameData.game().getTeamTurn();
                boolean checkmate = gameData.game().isInCheckmate(opponent);
                boolean stalemate = gameData.game().isInStalemate(opponent);
                if (checkmate) {
                    gameData.game().setWinner(command.getTeamColor());
                    gameData.game().setGameOver(true);
                } else if (stalemate) {
                    gameData.game().setGameOver(true);
                }
                gameDAO.update(gameData);
                sendAll(new LoadGame(gameData));
                sendOthers(new Notification(command.getMove().toString()));
                if (checkmate) {
                    sendAll(new Notification(String.format("Game over: %s won!", result.auth().username())));
                } else if (gameData.game().isInCheck(opponent)) {
                    sendAll(new Notification(opponent.toString() + " is in check"));
                } else if (stalemate) {
                    sendAll(new Notification("Game over: stalemate!"));
                }
            }
        } catch (DataAccessException | InvalidMoveException e) {
            sendRoot(new Error(e.getMessage()));
        }
    }
}
