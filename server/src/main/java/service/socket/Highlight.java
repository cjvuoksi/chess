package service.socket;

import chess.ChessMove;
import dataaccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import server.SessionInfo;
import websocket.commands.UserGameCommand;
import websocket.messages.Error;
import websocket.messages.Moves;

import java.util.Collection;

public class Highlight extends SocketService {
    public Highlight(UserGameCommand command, Session root, Collection<Session> sessions, SessionInfo info) {
        super(command, root, sessions, info);
    }

    @Override
    public void run() {
        try {
            GameData game = gameDAO.find(info.id());
            Collection<ChessMove> moves = game.game().validMoves(command.getMove().getStartPosition());
            sendRoot(new Moves(moves));

        } catch (DataAccessException e) {
            sendRoot(new Error(e.getMessage()));
        }
    }
}
