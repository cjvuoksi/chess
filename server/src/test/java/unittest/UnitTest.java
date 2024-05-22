package unittest;

import dataaccess.DAO;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;
import request.*;
import response.CreateResponse;
import response.LoginResponse;
import response.Response;
import service.HTTP.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UnitTest {
    private static UserData test = new UserData("test", "test", "test");
    private static UserData bad = new UserData("bad", "bad", "bad");
    private final String badAuth = "bad";
    private static String test_auth;
    private String auth;
    private static final ServiceFacade accessor = new ServiceFacade();
    private int gameID;

    @BeforeAll
    static void setUp() {
        try {
            new Clear().run(new Request());
            LoginResponse response = (LoginResponse) new Register().run(new RegisterRequest(test.username(), test.password(), test.email()));
            assertNull(response.getMessage(), "Error occurred: " + response.getMessage());
            test_auth = response.getAuthToken();
        } catch (DataAccessException e) {
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
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDownEach() {
        try {
            Response response = new Logout().run(new AuthRequest(auth));
            assertNull(response.getMessage(), "Error occurred: " + response.getMessage());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertAuthChange(int changeAmount, Runnable function) {
        assertDAOChange(changeAmount, accessor.getAuthDAO(), function);
    }

    private void assertUserChange(int changeAmount, Runnable function) {
        assertDAOChange(changeAmount, accessor.getUserDAO(), function);
    }

    private void assertGameChange(int changeAmount, Runnable function) {
        assertDAOChange(changeAmount, accessor.getGameDAO(), function);
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
        assertFail(endpoint, DataAccessException.class, request);
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
            } catch (DataAccessException e) {
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

}
