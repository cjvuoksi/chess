package request;

public class UpdateRequest extends RegisterRequest {
    protected String authorization;
    protected String oldPassword;

    public UpdateRequest(String username, String password, String oldPassword, String email, String authorization) {
        super(username, password, email);
        this.authorization = authorization;
        this.oldPassword = oldPassword;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getOldPassword() {
        return oldPassword;
    }
}
