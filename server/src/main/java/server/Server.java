package server;

import handler.*;
import service.*;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Server {

    public static void main(String[] args) {
        new Server().run(8080);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clearApp);
        // Register
        Spark.post("/user", this::register);
        // Login
        Spark.post("/session", this::login);
        // Logout
        Spark.delete("/session", this::logout);
        // List Games
        Spark.get("/game", this::listGames);
        // Create Game
        Spark.post("/game", this::createGame);
        // Join Game
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object joinGame(Request request, Response response) {
        return new JoinH(request, response, new Join()).run();
    }

    private Object createGame(Request request, Response response) {
        return new CreateH(request, response, new Create()).run();
    }

    private Object listGames(Request request, Response response) {
        return new ListH(request, response, new List()).run();
    }

    private Object logout(Request request, Response response) {
        return new LogoutH(request, response, new Logout()).run();
    }

    private Object login(Request request, Response response) {
        return new LoginH(request, response, new Login()).run();
    }

    private Object register(Request request, Response response) {
        return new RegisterH(request, response, new Register()).run();
    }

    private Object clearApp(Request request, Response response) {
        response.status(200);
        return new ClearH(request, response, new Clear()).run();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
