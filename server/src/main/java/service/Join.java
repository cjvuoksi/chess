package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import request.JoinRequest;
import request.Request;
import response.Response;

public class Join extends Service {
    @Override
    public Response run(Request req) throws DataAccessException {
        JoinRequest r = (JoinRequest) req;

        AuthData auth = authDAO.find(r.getAuthorization());
        if (auth == null) {
            throw new DataAccessException("Error: unauthorized", 401);
        }

        GameData game = gameDAO.find(r.getGameID());
        if (game == null) {
            throw new DataAccessException("Error: bad request (game not found)", 400);
        }

        if (r.getPlayerColor() == ChessGame.TeamColor.WHITE) {
            System.out.printf("%s = %s%n", auth.username(), game.whiteUsername());
            if (game.whiteUsername() != null && !game.whiteUsername().equals(auth.username())) {
                throw new DataAccessException("Error: already taken", 403);
            }

            GameData updated = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            gameDAO.update(game.gameID(), updated);
        } else if (r.getPlayerColor() == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() != null && !game.blackUsername().equals(auth.username())) {
                throw new DataAccessException("Error: already taken", 403);
            }

            GameData updated = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            gameDAO.update(game.gameID(), updated);
        }
        return new Response();
    }
}
