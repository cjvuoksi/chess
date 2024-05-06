package response;

public class LoginResponse extends Response {
    private final String authToken;
    private final String username;

    public LoginResponse(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }
}
