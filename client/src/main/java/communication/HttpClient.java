package communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import response.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {

    private final String url;

    private static final Gson serializer = new GsonBuilder().enableComplexMapKeySerialization().create();

    public HttpClient(String url) {
        this.url = url;
    }

    public Response getServerResponse(String path, String body, String method, Class<? extends Response> resultType, String... authorization)
            throws IOException {
        URL url = new URL(this.url + path); // FIXME remove the deprecation

        /*new URL("http", host, port, file);*/

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestMethod(method);

        for (String token : authorization) {
            connection.addRequestProperty("Authorization", token);
        }

        connection.addRequestProperty("Content-Type", "text/html");
        connection.addRequestProperty("Accept", "text/html");

        writeRequestBody(body, connection);

        return getResult(resultType, connection);
    }

    private Response getResult(Class<? extends Response> resultType, HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (InputStream responseBody = connection.getInputStream()) {
                return getResultFromStream(resultType, responseBody);
            }
        } else {
            try (InputStream responseBody = connection.getErrorStream()) {
                return getResultFromStream(resultType, responseBody);
            }
        }
    }

    private static Response getResultFromStream(Class<? extends Response> resultType, InputStream stream) {
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        return serializer.fromJson(inputStreamReader, resultType);
    }

    private static void writeRequestBody(String body, HttpURLConnection http) throws IOException {
        if (body != null && !body.isEmpty()) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }
}
