package client;

import chess.ChessGame;
import communication.HttpCommunicator;
import model.UserData;
import org.junit.jupiter.api.*;
import request.*;
import response.CreateResponse;
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
    private static int gameID = 0;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacadeExposer(port);
        http = serverFacade.getHTTPCommunicator();
        clear();
    }

    @BeforeEach
    void setUpEach() {
        if (token == null) {
            register();
        }
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
    public void testRegister() {
        assertNotNull(token);
    }


    private void register() {
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
        LoginResponse response = serverFacade.register(new RegisterRequest(testUser.username(), testUser.password(), testUser.email()));
        assertNotNull(response.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Login")
    public void login() {
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
        CreateResponse response = serverFacade.createGame(new CreateRequest(token, "game"));
        assertNull(response.getMessage());
        assertNotEquals(0, response.getGameID());
        gameID = response.getGameID();
    }

    @Test
    @Order(6)
    @DisplayName("Invalid Create")
    public void invalidCreate() {
        CreateResponse response = serverFacade.createGame(new CreateRequest("bad", null));
        assertNotNull(response.getMessage());
    }

    @Test
    @Order(7)
    @DisplayName("Join")
    public void join() {
        if (gameID == 0) {
            create();
        }

        Response response = serverFacade.join(new JoinRequest(token, ChessGame.TeamColor.WHITE, gameID));
        assertNull(response.getMessage());
        response = serverFacade.join(new JoinRequest(token, ChessGame.TeamColor.BLACK, gameID));
        assertNull(response.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Invalid Join")
    public void invalidJoin() {
        if (gameID == 0) {
            join();
        }

        Response response = serverFacade.join(new JoinRequest("bad", ChessGame.TeamColor.WHITE, gameID));
        assertNotNull(response.getMessage());
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
