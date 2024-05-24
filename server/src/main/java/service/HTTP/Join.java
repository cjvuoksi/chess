package service.HTTP;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import request.JoinRequest;
import request.Request;
import response.Response;

public class Join extends HTTPService {
    @Override
    public Response run(Request req) throws DataAccessException, ServiceException {
        JoinRequest r = (JoinRequest) req;

        AuthData auth = authDAO.find(r.getAuthorization());
        if (auth == null) {
            throw new ServiceException("Error: unauthorized", 401);
        }

        GameData game = gameDAO.find(r.getGameID());
        if (game == null) {
            throw new ServiceException("Error: bad request (game not found)", 400);
        }

        if (r.getPlayerColor() == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null && !game.whiteUsername().equals(auth.username())) {
                throw new ServiceException("Error: already taken", 403);
            }

            GameData updated = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            gameDAO.update(updated);
        } else if (r.getPlayerColor() == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() != null && !game.blackUsername().equals(auth.username())) {
                throw new ServiceException("Error: already taken", 403);
            }

            GameData updated = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            gameDAO.update(updated);
        } else {
            throw new ServiceException("Error: bad request", 400);
        }
        return new Response();
    }
}
