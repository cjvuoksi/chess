package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

public class UserCommand {
    protected CommandType commandType;

    private final String authToken;

    private final Integer id;

    private final ChessGame.TeamColor teamColor;

    protected ChessMove move;

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public UserCommand(Integer id, String authToken, ChessGame.TeamColor teamColor) {
        this.id = id;
        this.authToken = authToken;
        this.teamColor = teamColor;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getId() {
        return id;
    }

    public ChessMove getMove() {
        return move;
    }
}
