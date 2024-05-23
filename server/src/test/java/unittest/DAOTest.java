package unittest;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    @AfterAll
    @BeforeAll
    static void setUp() {
        clearDAOs();
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
    @DisplayName("Find All")
    void findAll() {
        if (canon == null) {
            create();
        }

        assertDoesNotThrow(() -> {
            Collection<UserData> users = user.findAll();
            Collection<GameData> games = game.findAll();
            Collection<AuthData> auths = auth.findAll();

            assertEquals(canon, new Result(users.iterator().next(), auths.iterator().next(), games.iterator().next()));
        });
    }

    @Test
    @Order(6)
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
    @Order(7)
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

    @Test
    @Order(8)
    @DisplayName("Delete")
    void delete() {
        if (canon == null) {
            create();
        }

        assertDoesNotThrow(() -> {
            user.delete(testUser.username());
            auth.delete(token);
            game.delete(gameID);
        }, "Delete failed");

        assertDoesNotThrow(() -> {
            Result testResult = getResult();
            assertNull(testResult.userData);
            assertNull(testResult.authData);
            assertNull(testResult.gameData);

        }, "Deleted item found in db");

        assertDoesNotThrow(this::create, "Error in recreating deleted items");
    }

    @Test
    @Order(9)
    @DisplayName("Invalid Delete")
    void invalidDelete() {
        try {
            assertEquals(0, user.delete(bad), "Delete succeeded for bad username");
            assertEquals(0, auth.delete(bad), "Delete succeeded for bad token");
            assertEquals(0, game.delete(0), "Delete succeeded for bad id");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(10)
    @DisplayName("Clear")
    void clear() {
        clearDAOs();
        clearDAOs();
    }

    private static void clearDAOs() {
        assertDoesNotThrow(() -> {
            user.clear();
            auth.clear();
            game.clear();
        });
    }

    @Test
    @Order(11)
    @DisplayName("Invalid Find All")
    void InvalidFindAll() {
        assertDoesNotThrow(() -> {
            assertEmpty(user.findAll());
            assertEmpty(auth.findAll());
            assertEmpty(game.findAll());
        });
    }

    private void assertEmpty(Collection<? extends Record> all) {
        assertTrue(all.isEmpty());
    }

    private static Result getResult() throws DataAccessException {
        UserData userData = user.find(testUser.username());
        AuthData authData = auth.find(token);
        GameData gameData = game.find(gameID);
        return new Result(userData, authData, gameData);
    }

    private record Result(UserData userData, AuthData authData, GameData gameData) {
    }

}
