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
    final String username;
    final String authToken;
    final ArrayList<Integer> games = new ArrayList<>();

    public PostLogin(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    @Override
    protected String getHelp() {
        return """
                logout (q): logs out of the server
                create (c): creates a new game
                list (l): lists the games on the server
                play (j): joins a game as a player
                watch (w): joins a game as an observer
                help (h): displays this help screen
                """;
    }

    @Override
    protected void exit() throws SwitchException {
        Response res = server.logout(new AuthRequest(authToken));
        if (res.getMessage() != null) {
            print(res.getMessage());
        }

        throw new SwitchException(SwitchException.exceptionType.LOGOUT);
    }

    @Override
    protected void menu() {
        print(String.format("Welcome %s!", username));
    }

    @Override
    protected void evaluate(String s) throws SwitchException {
        if (s.equalsIgnoreCase("leave")) exit();
        if (s.equalsIgnoreCase("c") || s.equalsIgnoreCase("create")) create();
        else if (s.equalsIgnoreCase("l") || s.equalsIgnoreCase("list")) list();
        else if (s.equalsIgnoreCase("j") || s.equalsIgnoreCase("play")) play();
        else if (s.equalsIgnoreCase("w") || s.equalsIgnoreCase("watch")) watch();
        else {
            printError(String.format("Invalid command: %s", s));
            help();
        }
    }

    private void create() {
        String name = promptInput("Game name: ");

        CreateResponse res = server.createGame(new CreateRequest(authToken, name));
        if (res.getMessage() != null) {
            printError(res.getMessage());
        } else {
            print("Game created");
            games.add(res.getGameID());
        }
    }

    private void list() {
        ListResponse res = server.listGames(new AuthRequest(authToken));
        if (res.getMessage() != null) {
            printError(res.getMessage());
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

    private void join(Boolean asPlayer) throws SwitchException {
        list();
        if (games.isEmpty()) {
            print("No games in the data base");
            return;
        }
        String num = promptInput("Enter number to play or (q) to escape: ");
        if (num == null || num.equalsIgnoreCase("q")) {
            return;
        }
        int gameNum;
        try {
            gameNum = Integer.parseInt(num, 10);
        } catch (NumberFormatException e) {
            printError(String.format("Invalid number: %s", num));
            return;
        }
        if (gameNum > games.size() || gameNum < 1) {
            printError("Invalid number entered");
            return;
        }
        ChessGame.TeamColor teamColor = null;
        if (asPlayer) {
            teamColor = getTeamColor();
            if (teamColor == null) {
                return;
            }
        }

        Response res = server.join(new JoinRequest(authToken, teamColor, games.get(gameNum - 1)));

        if (res.getMessage() != null && teamColor != null && !res.getMessage().contains("Bad")) {
            printError(res.getMessage());
        } else {
            throw new SwitchException(SwitchException.exceptionType.PLAY, new JoinRequest(authToken, teamColor, games.get(gameNum - 1)));
        }
    }

    private void play() throws SwitchException {
        join(true);
    }

    private void watch() throws SwitchException {
        join(false);
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

        printError("Invalid team color entered");
        return null;
    }
}
