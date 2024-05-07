package request;

public class AuthRequest extends Request {
    protected String authorization;

    public AuthRequest() {
    }

    public AuthRequest(String authorization) {
        this.authorization = authorization;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
}
