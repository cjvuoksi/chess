package unittest;

import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;
import request.AuthRequest;
import request.RegisterRequest;
import request.Request;
import request.UserRequest;
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

    void assertAuthChange(int changeAmount, Runnable function) {
        try {
            int startCount = accessor.getAuthDAO().findAll().size();
            function.run();
            assertEquals(accessor.getAuthDAO().findAll().size(), startCount + changeAmount);
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
}
