package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GameDao extends DAO<GameData, Integer> {
    private final Gson serializer = new Gson();

    @Override
    protected GameData getResult(ResultSet res) throws SQLException {
        return new GameData(Integer.parseInt(res.getString("id")),
                res.getString("white_user"),
                res.getString("black_user"),
                res.getString("game_name"),
                serializer.fromJson(res.getString("game"), ChessGame.class));
    }

    @Override
    protected String[] getArgs(GameData data) {
        return new String[]{String.valueOf(data.gameID()), data.whiteUsername(), data.blackUsername(), data.gameName(), serializer.toJson(data.game())};
    }
}
