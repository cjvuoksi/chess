package service;

import chess.ChessGame;
import dataaccess.DAO;
import dataaccess.DataAccessException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import request.*;
import response.CreateResponse;
import response.ListResponse;
import response.LoginResponse;
import response.Response;
import service.HTTP.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTest {
    private static UserData test = new UserData("test", "test", "test");
    private static UserData bad = new UserData("bad", "bad", "bad");
    private static String badAuth = "bad";
    private static String test_auth;
    private String auth;
    private static final ServiceFacade ACCESSOR = new ServiceFacade();
    private static Integer gameID;

    @BeforeAll
    static void setUp() {
        try {
            new Clear().run(new Request());
            LoginResponse response = (LoginResponse) new Register().run(new RegisterRequest(test.username(), test.password(), test.email()));
            assertNull(response.getMessage(), "Error occurred: " + response.getMessage());
            test_auth = response.getAuthToken();
        } catch (DataAccessException | ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUpEach() {
        try {
            LoginResponse response = (LoginResponse) new Login().run(new UserRequest(test.username(), test.password()));
            assertNull(response.getMessage(), "Error occurred for valid login: " + response.getMessage());
            assertNotEquals(response.getAuthToken(), test_auth, "Expected unique auth token");
            assertEquals(response.getUsername(), test.username(), "Expected usernames to match");
            if (response.getAuthToken() != null) {
                auth = response.getAuthToken();
            }
        } catch (DataAccessException | ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDownEach() {
        try {
            if (ACCESSOR.getAuthDAO().findAll().isEmpty()) {
                return;
            }
            Response response = new Logout().run(new AuthRequest(auth));
            assertNull(response.getMessage(), "Error occurred: " + response.getMessage());
        } catch (DataAccessException | ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertAuthChange(int changeAmount, Runnable function) {
        assertDAOChange(changeAmount, ACCESSOR.getAuthDAO(), function);
    }

    private void assertUserChange(int changeAmount, Runnable function) {
        assertDAOChange(changeAmount, ACCESSOR.getUserDAO(), function);
    }

    private void assertGameChange(int changeAmount, Runnable function) {
        assertDAOChange(changeAmount, ACCESSOR.getGameDAO(), function);
    }

    private void assertDAOChange(int changeAmount, DAO dao, Runnable function) {
        try {
            int startCount = dao.findAll().size();
            function.run();
            assertEquals(dao.findAll().size(), startCount + changeAmount);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertFail(HTTPService endpoint, Request request) {
        assertFail(endpoint, ServiceException.class, request);
    }

    private void assertFail(HTTPService endpoint, Class<? extends Exception> exception, Request request) {
        assertThrows(exception, () -> endpoint.run(request));
    }

    @Test
    @Order(1)
    @DisplayName("Valid Login")
    void login() {
        String tmp = auth;
        assertAuthChange(1, this::setUpEach);
        assertNotEquals(tmp, auth, "Expected unique auth token");
    }

    @Test
    @Order(2)
    @DisplayName("Invalid login")
    void invalidLogin() {
        assertAuthChange(0, () -> {
            assertFail(new Login(), new UserRequest(bad.username(), bad.password()));
            assertFail(new Login(), new UserRequest(null, bad.password()));
            assertFail(new Login(), new UserRequest(bad.username(), null));
            assertFail(new Login(), new UserRequest(null, null));
        });
    }

    @Test
    @Order(3)
    @DisplayName("Valid Logout")
    void logout() {
        assertAuthChange(-1, this::tearDownEach);
        setUpEach();
    }

    @Test
    @Order(4)
    @DisplayName("Invalid Logout")
    void invalidLogout() {
        assertAuthChange(0, () -> {
            assertFail(new Logout(), new AuthRequest(badAuth));
            assertFail(new Logout(), new AuthRequest(null));
        });
    }

    @Test
    @Order(5)
    @DisplayName("Valid Register")
    void register() {
        assertUserChange(1, () -> {
            assertDoesNotThrow(() -> new Register().run(new RegisterRequest("temp", "temp", "temp")));
        });
    }

    @Test
    @Order(6)
    @DisplayName("Invalid Register")
    void invalidRegister() {
        assertUserChange(0, () -> {
            assertFail(new Register(), new RegisterRequest(test.username(), test.password(), test.email()));
            assertFail(new Register(), new RegisterRequest(test.username(), null, null));
            assertFail(new Register(), new RegisterRequest(null, test.password(), null));
            assertFail(new Register(), new RegisterRequest(null, null, test.email()));
            assertFail(new Register(), new RegisterRequest(test.username(), test.password(), null));
            assertFail(new Register(), new RegisterRequest(null, test.password(), test.email()));
            assertFail(new Register(), new RegisterRequest(test.username(), null, test.email()));
        });
    }

    @Test
    @Order(7)
    @DisplayName("Valid Create")
    void create() {
        assertGameChange(1, () -> {
            try {
                CreateResponse response = (CreateResponse) new Create().run(new CreateRequest(auth, "game"));
                assertNull(response.getMessage());
                gameID = response.getGameID();
                GameData game = ACCESSOR.getGameDAO().find(gameID);
                assertNotNull(game);
                assertEquals("game", game.gameName());
                assertEquals(gameID, game.gameID());
            } catch (DataAccessException | ServiceException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    @Order(8)
    @DisplayName("Invalid Create")
    void invalidCreate() {
        assertGameChange(0, () -> {
            assertFail(new Create(), new CreateRequest(badAuth, "game"));
            assertFail(new Create(), new CreateRequest(null, "game"));
            assertFail(new Create(), new CreateRequest(auth, null));
        });
    }

    @Test
    @Order(9)
    @DisplayName("Valid List")
    void list() {
        try {
            if (ACCESSOR.getGameDAO().findAll().isEmpty()) {
                create();
            }

            ListResponse response = (ListResponse) new List().run(new AuthRequest(auth));
            assertFalse(response.getGames().isEmpty());
            assertEquals(response.getGames().size(), 1);
        } catch (DataAccessException | ServiceException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    @Order(10)
    @DisplayName("Invalid List")
    void invalidList() {
        assertFail(new List(), new AuthRequest(badAuth));
        assertFail(new List(), new AuthRequest(null));
    }

    @Test
    @Order(11)
    @DisplayName("Valid Join")
    void join() {
        assertDoesNotThrow(() -> {
            if (ACCESSOR.getGameDAO().findAll().isEmpty()) {
                create();
            }
            LoginResponse response = (LoginResponse) new Register().run(new RegisterRequest(bad.username(), bad.password(), bad.email()));
            badAuth = response.getAuthToken();
            new Join().run(new JoinRequest(badAuth, ChessGame.TeamColor.BLACK, gameID));
            new Join().run(new JoinRequest(auth, ChessGame.TeamColor.WHITE, gameID));
            GameData updated = ACCESSOR.getGameDAO().find(gameID);
            assertNotNull(updated);
            assertEquals(bad.username(), updated.blackUsername());
            assertEquals(test.username(), updated.whiteUsername());

            assertDoesNotThrow(() -> new Join().run(new JoinRequest(badAuth, ChessGame.TeamColor.BLACK, gameID)));
            assertDoesNotThrow(() -> new Join().run(new JoinRequest(auth, ChessGame.TeamColor.WHITE, gameID)));
        });
    }

    @Test
    @Order(12)
    @DisplayName("Invalid Join")
    void invalidJoin() {
        try {
            if (ACCESSOR.getGameDAO().findAll().isEmpty()) {
                join();
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        assertFail(new Join(), new JoinRequest(badAuth, ChessGame.TeamColor.WHITE, gameID));
        assertFail(new Join(), new JoinRequest(auth, ChessGame.TeamColor.BLACK, gameID));
        assertFail(new Join(), DataAccessException.class, new JoinRequest(null, null, gameID));
    }

    @Test
    @Order(13)
    @DisplayName("Clear")
    void clear() {
        assertDoesNotThrow(() -> {
            new Clear().run(new Request());
            new Clear().run(new Request());
        });
        try {
            assertTrue(ACCESSOR.getGameDAO().findAll().isEmpty());
            assertTrue(ACCESSOR.getAuthDAO().findAll().isEmpty());
            assertTrue(ACCESSOR.getUserDAO().findAll().isEmpty());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void tearDown() {
        assertDoesNotThrow(() -> {
            new Clear().run(new Request());
        });
    }
}
