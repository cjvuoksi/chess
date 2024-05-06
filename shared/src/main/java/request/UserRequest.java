package request;

public class UserRequest extends Request {
    protected final String username;
    protected final String password;

    public UserRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
