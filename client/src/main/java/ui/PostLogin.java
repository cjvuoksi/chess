package ui;

import chess.ChessGame;
import model.GameData;
import request.AuthRequest;
import request.CreateRequest;
import request.JoinRequest;
import response.CreateResponse;
import response.ListResponse;
import response.Response;

import java.util.ArrayList;

public class PostLogin extends UI {
    String username;
    String authToken;
    ArrayList<Integer> games = new ArrayList<>();

    public PostLogin(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    @Override
    protected String getHelp() {
        return """
                logout (l): logs out of the server
                create (c): creates a new game
                list (g): lists the games on the server
                play (p): joins a game as a player
                watch (w): joins a game as an observer
                help (h): displays this help screen
                """;
    }

    @Override
    protected void exit() throws SwitchException {
        throw new SwitchException(SwitchException.exceptionType.LOGOUT);
    }

    @Override
    protected void menu() {
        print(String.format("Welcome %s!", username));
    }

    @Override
    protected void evaluate(String s) throws SwitchException {
        if (s.equalsIgnoreCase("l")) exit();
        if (s.equalsIgnoreCase("c") || s.equalsIgnoreCase("create")) create();
        else if (s.equalsIgnoreCase("g") || s.equalsIgnoreCase("list")) list();
        else if (s.equalsIgnoreCase("p") || s.equalsIgnoreCase("play")) play();
        else if (s.equalsIgnoreCase("w") || s.equalsIgnoreCase("watch")) watch();
    }

    private void create() {
        String name = promptInput("Game name: ");

        CreateResponse res = server.createGame(new CreateRequest(authToken, name));
        if (res.getMessage() != null) {
            print(res.getMessage());
        } else {
            print("Game created");
            games.add(res.getGameID());
        }
    }

    private void list() {
        ListResponse res = server.listGames(new AuthRequest(authToken));
        if (res.getMessage() != null) {
            print(res.getMessage());
        } else {
            games.clear();
            int i = 1;
            for (GameData item : res.getGames()) {
                games.add(item.gameID());
                String none = EscapeSequences.SET_TEXT_ITALIC + "none" + EscapeSequences.RESET_TEXT_ITALIC;
                String white = item.whiteUsername() == null ? none : item.whiteUsername();
                String black = item.blackUsername() == null ? none : item.blackUsername();
                print(String.format("""
                        %d) %s
                            white: %s
                            black: %s
                        """, i, item.gameName(), white, black));
                i++;
            }
        }
    }

    private void play() throws SwitchException {
        list();
        String num = promptInput("Enter number to play or (q) to escape: ");
        if (num == null || num.equalsIgnoreCase("q")) {
            return;
        }
        int gameNum = Integer.parseInt(num);
        if (gameNum > games.size() || gameNum < 1) {
            print("Invalid number entered");
            return;
        }

        ChessGame.TeamColor teamColor = getTeamColor();
        if (teamColor == null) {
            return;
        }

        Response res = server.join(new JoinRequest(authToken, teamColor, games.get(gameNum - 1)));

        if (res.getMessage() != null) {
            print(res.getMessage());
        } else {
            throw new SwitchException(SwitchException.exceptionType.PLAY);
        }
    }

    private ChessGame.TeamColor getTeamColor() {
        String team = promptInput("Enter team color (b)lack/(w)hite: ");
        if (team == null || team.equalsIgnoreCase("quit") || team.equalsIgnoreCase("q")) {
            return null;
        }

        if (team.equalsIgnoreCase("black") || team.equalsIgnoreCase("b")) {
            return ChessGame.TeamColor.BLACK;
        }
        if (team.equalsIgnoreCase("white") || team.equalsIgnoreCase("w")) {
            return ChessGame.TeamColor.WHITE;
        }

        print("Invalid team color entered");
        return null;
    }

    private void watch() {
    }
}
