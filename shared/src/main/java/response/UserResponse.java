package response;

public class UserResponse extends Response {
    private final String username;
    private final String email;

    public UserResponse(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}