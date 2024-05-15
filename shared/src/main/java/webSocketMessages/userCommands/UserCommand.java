package webSocketMessages.userCommands;

public class UserCommand {
    public UserCommand(Integer id, String authToken) {
        this.id = id;
        this.authToken = authToken;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    protected CommandType commandType;

    private final String authToken;

    private final Integer id;

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getId() {
        return id;
    }
}
