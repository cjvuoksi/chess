package communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import request.*;
import response.CreateResponse;
import response.ListResponse;
import response.LoginResponse;
import response.Response;
import ui.Observer;
import webSocketMessages.userCommands.Leave;
import webSocketMessages.userCommands.MakeMove;
import webSocketMessages.userCommands.Resign;

public class ServerFacade {

    private final Gson serializer = new GsonBuilder().enableComplexMapKeySerialization().create();

    private final HttpCommunicator http = new HttpCommunicator();

    private WebSocketCommunicator websocket = new WebSocketCommunicator();

    public static final String url = "http://localhost:8080/";

    //Prelogin
    public LoginResponse register(RegisterRequest request) {
        return http.register(request);
    }

    public LoginResponse login(UserRequest request) {
        return http.login(request);
    }

    //Postlogin
    public Response logout(AuthRequest request) {
        return http.logout(request);
    }

    public CreateResponse createGame(CreateRequest request) {
        return http.createGame(request);
    }

    public Response join(JoinRequest request) {
        return http.join(request);
    }

    public ListResponse listGames(AuthRequest request) {
        return http.listGames(request);
    }

    public void close() {
        websocket.deinit();
    }

    //WebSocket
    public void upgradeConnection(Observer observer, JoinRequest request) {
        websocket.initialize(observer, request);
    }

    public void leave(Leave leave) throws Exception {
        websocket.send(leave);
    }

    public void move(MakeMove move) throws Exception {
        websocket.send(move);
    }

    public void resign(Resign resign) throws Exception {
        websocket.send(resign);
    }
}
