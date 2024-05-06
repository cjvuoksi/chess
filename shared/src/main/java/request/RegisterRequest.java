package request;

public class RegisterRequest extends UserRequest {
    protected final String email;

    public RegisterRequest(String username, String password, String email) {
        super(username, password);
        this.email = email;
    }
}
