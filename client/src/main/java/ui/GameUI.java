package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class GameUI extends UI {
    ChessGame game = new ChessGame();
    private final String authToken;
    private final ChessGame.TeamColor teamColor;

    public GameUI(String authToken, ChessGame.TeamColor teamColor) {
        this.authToken = authToken;
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
        throw new SwitchException(SwitchException.exceptionType.LEAVE);
    }

    @Override
    protected void menu() {
        print("GAME"); // ADD to context switcher
    }

    @Override
    protected void evaluate(String s) throws SwitchException {
        if (s.equals("q") || s.equalsIgnoreCase("leave")) {
            exit();
        }
        if (s.equals("m") || s.equalsIgnoreCase("move")) {
            move();
        } else if (s.equals("d") || s.equalsIgnoreCase("redraw")) {
            redraw();
        } else if (s.equals("r") || s.equalsIgnoreCase("resign")) {
            resign();
        } else if (s.equals("l") || s.equalsIgnoreCase("lm")) {
            highlight();
        } else if (parsePosition(s) != null) {
            move(parsePosition(s));
        }
    }

    private void move(ChessPosition chessPosition) {
        ChessPiece piece = game.getBoard().getPiece(chessPosition);
        if (piece == null || piece.getTeamColor() != teamColor || piece.getTeamColor() != game.getTeamTurn()) {
            print("Bad start position");
        }

        highlight(chessPosition);

        promptInput("End position");
    }

    private void highlight() {
        ChessPosition position = parsePosition(promptInput("Enter position: "));

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
        return new ChessPosition(s.charAt(1) - '0', s.charAt(0) - 'b');
    }

    private void resign() {
    }

    private void redraw() {
    }

    private void move() {
    }

    private void draw() {
        Drawer bd = new Drawer(this);
        bd.printBoard(teamColor);
    }
}
