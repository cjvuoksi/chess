package communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import request.*;
import response.CreateResponse;
import response.ListResponse;
import response.LoginResponse;
import response.Response;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class HttpCommunicator {
    private final Gson serializer = new GsonBuilder().enableComplexMapKeySerialization().create();

    private Response executeService(Request request, Class<? extends Response> resultType, String path, String method) {
        try {
            String body = serializer.toJson(request);

            if (request.getClass() == AuthRequest.class) {
                body = null;
            }
            HttpClient client = new HttpClient(ServerFacade.url);

            if (request instanceof AuthRequest) {
                return client.getServerResponse(path, body, method, resultType, ((AuthRequest) request).getAuthorization());
            }
            return client.getServerResponse(path, body, method, resultType);

        } catch (IOException e) {
            return buildResultFromError(resultType, e);
        }
    }

    private static Response buildResultFromError(Class<? extends Response> resultType, IOException e) {
        try {
            Constructor<? extends Response> resultConstructor = resultType.getDeclaredConstructor(String.class);
            return resultConstructor.newInstance(e.getMessage());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    //Prelogin
    public LoginResponse register(RegisterRequest request) {
        return (LoginResponse) executeService(request, LoginResponse.class, "user", "POST");
    }

    public LoginResponse login(UserRequest request) {
        return (LoginResponse) executeService(request, LoginResponse.class, "session", "POST");
    }

    //Postlogin
    public Response logout(AuthRequest request) {
        return executeService(request, Response.class, "session", "DELETE");
    }

    public CreateResponse createGame(CreateRequest request) {
        return (CreateResponse) executeService(request, CreateResponse.class, "game", "POST");
    }

    public Response join(JoinRequest request) {
        return executeService(request, Response.class, "game", "PUT");
    }

    public ListResponse listGames(AuthRequest request) {
        return (ListResponse) executeService(request, ListResponse.class, "game", "GET");
    }
}
