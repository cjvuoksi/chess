package client;

import communication.ServerFacade;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static UserData testUser = new UserData("test", "test", "test");
    private static String token;
    private static int gameID;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @Order(1)
    @DisplayName("Register")
    public void register() {

    }

    @Test
    @Order(2)
    @DisplayName("Register")
    public void invalidRegister() {

    }

}
