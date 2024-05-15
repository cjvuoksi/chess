package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;

public class GameUI extends UI implements Observer {
    ChessGame game = new ChessGame();
    int gameID;

    private final ChessGame.TeamColor teamColor;
    private boolean exit = false;

    public GameUI(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    @Override
    protected String getHelp() {
        return """
                redraw (d): redraws the chess board
                leave (q): leaves the current game allowing another to join
                move (m): makes a move
                resign (r): resigns the current game
                lm (l): highlights legal moves
                help (h): prints this text
                """;
    }

    @Override
    protected void exit() throws SwitchException {
        close();
        throw new SwitchException(SwitchException.exceptionType.LEAVE);
    }

    @Override
    protected void menu() {
        redraw();
    }

    @Override
    protected void evaluate(String s) throws SwitchException {
        if (s.equals("q") || s.equalsIgnoreCase("leave")) {
            exit();
        }
        if (s.equals("m") || s.equalsIgnoreCase("move")) {
            move();
            redraw();
        } else if (s.equals("d") || s.equalsIgnoreCase("redraw")) {
            redraw();
        } else if (s.equals("r") || s.equalsIgnoreCase("resign")) {
            resign();
        } else if (s.equals("l") || s.equalsIgnoreCase("lm")) {
            highlight();
        } else if (parsePosition(s) != null) {
            move(parsePosition(s));
            redraw();
        } else {
            redraw();
            print(String.format("Invalid command: %s", s));
            help();
        }
    }

    private void move(ChessPosition chessPosition) throws SwitchException {
        if (chessPosition == null) {
            print("Invalid position");
            return;
        }

        ChessPiece piece = game.getBoard().getPiece(chessPosition);
        if (piece == null || piece.getTeamColor() != teamColor || piece.getTeamColor() != game.getTeamTurn()) {
            print("Bad start position");
            return;
        }

        highlight(chessPosition);

        ChessPosition end = parsePosition(promptInput("End position"));

        if (end == null) {
            print("Invalid end position");
        }

        ChessMove move = new ChessMove(chessPosition, end, null);
        if (!game.validMoves(chessPosition).contains(move)) {
            print("Invalid move");
            return;
        }

        if (exit) {
            exit();
        }
    }

    private void highlight() {
        ChessPosition position = parsePosition(promptInput("Enter position: "));
        if (position == null || !position.isValid()) {
            print("Invalid position");
            return;
        }

        highlight(position);
    }

    private void highlight(ChessPosition position) {
        Drawer bd = new Drawer(this);
        bd.highlightValidMoves(position, teamColor);
    }

    private ChessPosition parsePosition(String s) {
        if (s.length() != 2) {
            return null;
        }
        if (!((s.charAt(0) >= 'a' || s.charAt(0) <= 'h') && (s.charAt(0) >= 'A' || s.charAt(0) <= 'H'))) {
            return null;
        }
        if (!(s.charAt(1) >= '1' || s.charAt(1) <= '8')) {
            return null;
        }
        ChessPosition pos = new ChessPosition(s.charAt(1) - '0', s.charAt(0) - ('a' - 1));

        if (pos.isValid()) {
            return pos;
        }
        return null;
    }

    private void resign() throws SwitchException {

        if (exit) {
            exit();
        }
    }

    private void redraw() {
        clearScreen();
        draw();
    }

    private void move() throws SwitchException {
        redraw();
        if (game.getTeamTurn() != teamColor) {
            print("It's not your turn");
        }

        move(parsePosition(promptInput("Start position: ")));
    }

    private void draw() {
        Drawer bd = new Drawer(this);
        bd.printBoard(teamColor);
    }

    @Override
    public void clearScreen() {
        super.clearScreen();
        draw();
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                game = ((LoadGame) message).getGameData().game();
                clearScreen();
            }
            case ERROR -> {
                clearScreen();
                print(((Error) message).getMessage());
            }
            case NOTIFICATION -> {
                clearScreen();
                print(((Notification) message).getMessage());
            }
            default -> {
                clearScreen();
                print(message.toString());
            }
        }
    }

    @Override
    public void notifyClosed() {
        exit = true;
    }

    private void close() {
        server.close();
    }
}
