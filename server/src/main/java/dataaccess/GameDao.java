package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.lang.Integer.valueOf;

public class GameDao extends DAO<GameData, Integer> {

    private final Gson serializer = new GsonBuilder().enableComplexMapKeySerialization().create();
    private Boolean make;

    public void setMake(Boolean make) {
        this.make = make;
    }

    {
        createStatement = "INSERT INTO game (white_user, black_user, game_name, game) VALUES (?, ?, ?, ?)";
        findStatement = "SELECT * FROM game WHERE id = ?";
        updateStatement = "UPDATE game SET white_user = ?, black_user = ?, game_name = ?, game = ? WHERE id = ?";
        deleteStatement = "DELETE FROM game WHERE id = ?";
        findallStatement = "SELECT * FROM game";
        clearStatement = "DELETE FROM game";
    }

    @Override
    protected GameData getResult(ResultSet res) throws SQLException {
        return new GameData(valueOf(res.getString("id")),
                res.getString("white_user"),
                res.getString("black_user"),
                res.getString("game_name"),
                serializer.fromJson(res.getString("game"), ChessGame.class));
    }

    @Override
    protected String[] getArgs(GameData data) {
        if (make) {
            make = false;
            return new String[]{data.whiteUsername(), data.blackUsername(), data.gameName(), serializer.toJson(data.game())};
        }

        return new String[]{data.whiteUsername(), data.blackUsername(), data.gameName(), serializer.toJson(data.game()), String.valueOf(data.gameID())};
    }
}
