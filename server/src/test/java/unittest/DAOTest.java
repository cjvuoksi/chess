package unittest;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DAOTest {
    static private final UserDAO user = new UserDAO();
    static private final AuthDAO auth = new AuthDAO();
    static private final GameDao game = new GameDao();
    static private final UserData testUser = new UserData("test", "test", "test");
    static private final String token = UUID.randomUUID().toString();
    static private final AuthData testAuth = new AuthData(token, testUser.username());
    static private GameData testGame;
    static private Result canon;
    static private int gameID;
    static private final String bad = "bad";
    static private final int badID = 0;


    @BeforeAll
    static void setUp() {
        assertDoesNotThrow(() -> {
            user.clear();
            auth.clear();
            game.clear();
        });
    }

    @Test
    @Order(1)
    @DisplayName("Create")
    void create() {
        assertDoesNotThrow(() -> {
            user.create(testUser);
            auth.create(testAuth);
            game.setMake(true);
            gameID = game.create(new GameData(0, null, null, "game", new ChessGame()));
            testGame = new GameData(gameID, null, null, "game", new ChessGame());
            canon = new Result(testUser, testAuth, testGame);
        });
        try {
            Result result = getResult();
            assertEquals(canon, result);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(2)
    @DisplayName("Invalid Create")
    void invalidCreate() {
        assertThrows(DataAccessException.class, () ->
                user.create(new UserData(null, null, null))
        );
        assertThrows(DataAccessException.class, () ->
                auth.create(new AuthData(null, null))
        );
        assertThrows(DataAccessException.class, () ->
                game.create(new GameData(0, null, null, null, null))
        );
    }

    @Test
    @Order(3)
    @DisplayName("Find")
    void find() {
        assertDoesNotThrow(() -> {
            Result res = getResult();
            assertNotNull(res.authData);
            assertNotNull(res.gameData);
            assertNotNull(res.userData);
        });
    }

    @Test
    @Order(4)
    @DisplayName("Invalid Find")
    void invalidFind() {
        assertThrows(DataAccessException.class, () ->
                user.find(null)
        );
        assertThrows(DataAccessException.class, () ->
                auth.find(null)
        );
        assertThrows(DataAccessException.class, () ->
                game.find(null)
        );
        try {
            assertNull(user.find(bad));
            assertNull(auth.find(bad));
            assertNull(game.find(badID));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(5)
    @DisplayName("Update")
    void update() {
        assertDoesNotThrow(() -> {
            game.update(new GameData(gameID, testAuth.username(), testAuth.username(), testGame.gameName(), testGame.game()));
        });
        try {
            Result res = getResult();
            assertEquals(testUser.username(), res.gameData.blackUsername());
            assertEquals(testUser.username(), res.gameData.whiteUsername());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(6)
    @DisplayName("Invalid Update")
    void invalidUpdate() {
        assertThrows(NullPointerException.class, () -> {
            game.update(null);
        });
        assertThrows(NullPointerException.class, () -> {
            user.update(null);
        });
        assertThrows(NullPointerException.class, () -> {
            auth.update(null);
        });
    }


    private static Result getResult() throws DataAccessException {
        UserData userData = user.find(testUser.username());
        AuthData authData = auth.find(token);
        GameData gameData = game.find(gameID);
        Result result = new Result(userData, authData, gameData);
        return result;
    }

    private record Result(UserData userData, AuthData authData, GameData gameData) {
    }

}
