package client;

import communication.HttpCommunicator;
import model.UserData;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
import request.Request;
import request.UserRequest;
import response.LoginResponse;
import response.Response;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    private static ServerFacadeExposer serverFacade;
    private static HttpCommunicator http;
    private static UserData testUser = new UserData("test", "test", "test");
    private static String token;
    private static String token2;
    private static int gameID;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacadeExposer(port);
        http = serverFacade.getHTTPCommunicator();
        clear();
    }

    private static void clear() {
        http.executeService(new Request(), Response.class, "db", "DELETE");
    }

    @AfterAll
    static void stopServer() {
        clear();
        server.stop();
    }


    @Test
    @Order(1)
    @DisplayName("Register")
    public void register() {
        LoginResponse response = serverFacade.register(new RegisterRequest(testUser.username(), testUser.password(), testUser.email()));
        assertNull(response.getMessage());
        assertEquals(testUser.username(), response.getUsername());
        assertNotNull(response.getAuthToken());
        token = response.getAuthToken();
    }

    @Test
    @Order(2)
    @DisplayName("Invalid Register")
    public void invalidRegister() {
        if (token == null) {
            register();
        }
        LoginResponse response = serverFacade.register(new RegisterRequest(testUser.username(), testUser.password(), testUser.email()));
        assertNotNull(response.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Login")
    public void login() {
        if (token == null) {
            register();
        }
        LoginResponse response = serverFacade.login(new UserRequest(testUser.username(), testUser.password()));
        assertNull(response.getMessage());
        assertEquals(testUser.username(), response.getUsername());
        assertNotEquals(token, response.getAuthToken());
        token2 = response.getAuthToken();
    }

    @Test
    @Order(4)
    @DisplayName("Invalid Login")
    public void invalidLogin() {
        LoginResponse response = serverFacade.login(new UserRequest("bad", "bad"));
        assertNotNull(response.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("Create")
    public void create() {

    }

    @Test
    @Order(6)
    @DisplayName("Invalid Create")
    public void invalidCreate() {

    }

    @Test
    @Order(7)
    @DisplayName("Join")
    public void join() {

    }

    @Test
    @Order(8)
    @DisplayName("Invalid Join")
    public void invalidJoin() {

    }

    @Test
    @Order(9)
    @DisplayName("List Games")
    public void listGames() {

    }

    @Test
    @Order(10)
    @DisplayName("Invalid List Games")
    public void invalidListGames() {

    }

    @Test
    @Order(11)
    @DisplayName("Logout")
    public void logout() {

    }

    @Test
    @Order(12)
    @DisplayName("Invalid Logout")
    public void invalidLogout() {

    }

}
