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

        } else if (r.getPlayerColor() == ChessGame.TeamColor.BLACK) {

        } else {
            return new Response(); //Observer
        }
    }
}
