package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DAOTest {
    static private final UserDAO USER = new UserDAO();
    static private final AuthDAO AUTH = new AuthDAO();
    static private final GameDao GAME = new GameDao();
    static private final UserData TEST_USER = new UserData("test", "test", "test");
    static private final String TOKEN = UUID.randomUUID().toString();
    static private final AuthData TEST_AUTH = new AuthData(TOKEN, TEST_USER.username());
    static private GameData testGame;
    static private Result canon;
    static private int gameID;
    static private final String BAD = "bad";
    static private final int BAD_ID = 0;

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
            USER.create(TEST_USER);
            AUTH.create(TEST_AUTH);
            GAME.setMake(true);
            gameID = GAME.create(new GameData(0, null, null, "game", new ChessGame()));
            testGame = new GameData(gameID, null, null, "game", new ChessGame());
            canon = new Result(TEST_USER, TEST_AUTH, testGame);
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
                USER.create(new UserData(null, null, null))
        );
        assertThrows(DataAccessException.class, () ->
                AUTH.create(new AuthData(null, null))
        );
        assertThrows(DataAccessException.class, () ->
                GAME.create(new GameData(0, null, null, null, null))
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
                USER.find(null)
        );
        assertThrows(DataAccessException.class, () ->
                AUTH.find(null)
        );
        assertThrows(DataAccessException.class, () ->
                GAME.find(null)
        );
        try {
            assertNull(USER.find(BAD));
            assertNull(AUTH.find(BAD));
            assertNull(GAME.find(BAD_ID));
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
            Collection<UserData> users = USER.findAll();
            Collection<GameData> games = GAME.findAll();
            Collection<AuthData> auths = AUTH.findAll();

            assertEquals(canon, new Result(users.iterator().next(), auths.iterator().next(), games.iterator().next()));
        });
    }

    @Test
    @Order(6)
    @DisplayName("Update")
    void update() {
        assertDoesNotThrow(() -> {
            GAME.update(new GameData(gameID, TEST_AUTH.username(), TEST_AUTH.username(), testGame.gameName(), testGame.game()));
        });
        try {
            Result res = getResult();
            assertEquals(TEST_USER.username(), res.gameData.blackUsername());
            assertEquals(TEST_USER.username(), res.gameData.whiteUsername());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(7)
    @DisplayName("Invalid Update")
    void invalidUpdate() {
        assertThrows(NullPointerException.class, () -> {
            GAME.update(null);
        });
        assertThrows(NullPointerException.class, () -> {
            USER.update(null);
        });
        assertThrows(NullPointerException.class, () -> {
            AUTH.update(null);
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
            USER.delete(TEST_USER.username());
            AUTH.delete(TOKEN);
            GAME.delete(gameID);
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
            assertEquals(0, USER.delete(BAD), "Delete succeeded for bad username");
            assertEquals(0, AUTH.delete(BAD), "Delete succeeded for bad token");
            assertEquals(0, GAME.delete(0), "Delete succeeded for bad id");
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
            USER.clear();
            AUTH.clear();
            GAME.clear();
        });
    }

    @Test
    @Order(11)
    @DisplayName("Invalid Find All")
    void InvalidFindAll() {
        assertDoesNotThrow(() -> {
            assertEmpty(USER.findAll());
            assertEmpty(AUTH.findAll());
            assertEmpty(GAME.findAll());
        });
    }

    private void assertEmpty(Collection<? extends Record> all) {
        assertTrue(all.isEmpty());
    }

    private static Result getResult() throws DataAccessException {
        UserData userData = USER.find(TEST_USER.username());
        AuthData authData = AUTH.find(TOKEN);
        GameData gameData = GAME.find(gameID);
        return new Result(userData, authData, gameData);
    }

    private record Result(UserData userData, AuthData authData, GameData gameData) {
    }

}
