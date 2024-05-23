package client;

import communication.HttpCommunicator;
import model.UserData;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
import request.Request;
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

    }

    @Test
    @Order(4)
    @DisplayName("Invalid Login")
    public void invalidLogin() {

    }

    @Test
    @Order(5)
    @DisplayName("Logout")
    public void logout() {

    }

    @Test
    @Order(6)
    @DisplayName("Invalid Logout")
    public void invalidLogout() {

    }

}
