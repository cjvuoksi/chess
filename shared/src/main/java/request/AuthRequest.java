package request;

public class AuthRequest extends Request {
    protected final String authorization;


    protected AuthRequest(String authorization) {
        this.authorization = authorization;
    }
}
