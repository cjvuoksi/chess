package request;

public class CreateRequest extends AuthRequest {
    private final String gameName;

    public CreateRequest(String authorization, String gameName) {
        super(authorization);
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }
}
