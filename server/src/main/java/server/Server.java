package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysql.cj.log.Slf4JLogger;
import handler.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.HTTP.*;
import service.socket.Connect;
import service.socket.Leave;
import service.socket.MakeMove;
import service.socket.Resign;
import spark.Request;
import spark.Response;
import spark.Spark;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@WebSocket
public class Server {

    private final Slf4JLogger log = new Slf4JLogger("Server");

    private final Gson serializer = new GsonBuilder().enableComplexMapKeySerialization().create();

    private final SessionManager sessionManager = new SessionManager();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.webSocket("/ws", server.Server.class);

        Spark.webSocket("/echo", server.Echo.class);

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

        Spark.internalServerError(serializer.toJson(new response.Response("Internal Server Error")));

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

    @OnWebSocketConnect
    public void open(Session session) {
        log.logInfo("Connected: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        log.logInfo(message);

        UserGameCommand userCommand = serializer.fromJson(message, UserGameCommand.class);
        if (userCommand.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            sessionManager.add(session, new SessionInfo(userCommand.getGameID(), userCommand.getTeamColor()));
        }


        websocketEndpoint(userCommand, session, sessionManager.get(session));
    }

    @OnWebSocketError
    public void onError(Throwable error) {
        if (TimeoutException.class == error.getClass()) {
            log.logInfo("Session timed out");
            return;
        }

        log.logError(error.getMessage());
//        log.logError(error.getCause());
        log.logError(error.getStackTrace());
        error.printStackTrace();
    }

    @OnWebSocketClose
    public synchronized void onClose(Session session, int code, String reason) {
        log.logInfo("On close: " + code + " " + reason);

        SessionInfo info = sessionManager.get(session);
        if (info != null) {
            sessionManager.remove(session, info.id());
        }
    }

    private void websocketEndpoint(UserGameCommand command, Session root, SessionInfo info) {
        switch (command.getCommandType()) {
            case CONNECT -> new Connect(command, root, sessionManager.get(command.getGameID()), info).run();
            case MAKE_MOVE -> new MakeMove(command, root, sessionManager.get(command.getGameID()), info).run();
            case LEAVE -> new Leave(command, root, sessionManager.get(command.getGameID()), info).run();
            case RESIGN -> new Resign(command, root, sessionManager.get(command.getGameID()), info).run();
            default -> {
                try {
                    root.getRemote().sendString(serializer.toJson(new Error("Unexpected value: " + command.getCommandType())));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @OnWebSocketFrame
    public void onFrame(org.eclipse.jetty.websocket.api.Session session, org.eclipse.jetty.websocket.api.extensions.Frame frame) {
        log.logInfo("Frame " + frame.getType() + " " + frame.getPayload());
    }
}
