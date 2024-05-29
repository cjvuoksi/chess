package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import websocket.commands.Leave;
import websocket.commands.MakeMove;
import websocket.commands.Resign;
import websocket.messages.ServerMessage;

import java.util.Collection;

public class GameUI extends UI implements Observer {
    ChessGame game;
    int gameID;
    String authToken;

    private final ChessGame.TeamColor teamColor;
    private boolean exit = false;

    public GameUI(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    @Override
    protected String getHelp() {
        return """
                redraw (d): redraws the chess board
                leave (Q): leaves the current game allowing another to join
                move (m): makes a move
                resign (r): resigns the current game
                lm (l): highlights legal moves
                help (h): prints this text
                quit (q): quits the current game
                    (keeps spot as player and leaves silently as an observer)
                """;
    }

    @Override
    protected void exit() throws SwitchException {
        close();
        throw new SwitchException(SwitchException.exceptionType.LEAVE);
    }

    @Override
    protected void menu() {
    }

    @Override
    protected void evaluate(String s) throws SwitchException {
        if (s.equals("Q") || s.equalsIgnoreCase("leave")) {
            leave();
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
        } else {
            redraw();
            printError(String.format("Invalid command: %s", s));
            help();
        }
    }

    private void leave() throws SwitchException {
        if (!exit) {
            try {
                server.leave(new Leave(gameID, authToken, teamColor));
            } catch (Exception e) {
                print(e.getMessage());
            }
        }
        exit();
    }

    private void move(ChessPosition chessPosition) throws SwitchException {
        if (chessPosition == null) {
            printError("Invalid position");
            return;
        }

        ChessPiece piece = game.getBoard().getPiece(chessPosition);
        if (piece == null || piece.getTeamColor() != teamColor || piece.getTeamColor() != game.getTeamTurn()) {
            printError("Bad start position");
            return;
        }

        highlight(chessPosition);

        ChessPosition end = parsePosition(promptInput("End position"));

        if (end == null) {
            redraw();
            printError("Invalid end position");
            return;
        }

        ChessMove move = getMove(chessPosition, end);

        if (!game.validMoves(chessPosition).contains(move)) {
            redraw();
            printError("Invalid move");
            return;
        }

        if (exit) {
            exit();
        }

        try {
            server.move(new MakeMove(gameID, authToken, teamColor, move));
        } catch (Exception e) {
            printError("Error occurred: " + e.getMessage());
        }
    }

    private ChessMove getMove(ChessPosition startPosition, ChessPosition endPosition) {
        ChessMove move = new ChessMove(startPosition, endPosition, null);
        if (game.getBoard().getPiece(startPosition).getPieceType() == ChessPiece.PieceType.PAWN) {
            move = getPawnPromotion(game.validMoves(startPosition), startPosition, endPosition);
        }
        return move;
    }

    private ChessMove getPawnPromotion(Collection<ChessMove> validMoves, ChessPosition startPosition, ChessPosition endPosition) {
        for (ChessMove validMove : validMoves) {
            if (validMove.getPromotionPiece() != null) {
                return getPromotionType(startPosition, endPosition);
            }
        }
        return new ChessMove(startPosition, endPosition, null);
    }

    private ChessMove getPromotionType(ChessPosition startPosition, ChessPosition endPosition) {
        print("1: Queen");
        print("2: Rook");
        print("3: Bishop");
        print("4: Knight");

        switch (promptInput("Enter promotion piece")) {
            case "2" -> {
                return new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK);
            }
            case "3" -> {
                return new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP);
            }
            case "4" -> {
                return new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT);
            }
            default -> {
                return new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN);
            }
        }
    }

    private void highlight() {
        ChessPosition position = parsePosition(promptInput("Enter position: "));
        if (position == null || !position.isValid()) {
            printError("Invalid position");
            return;
        }

        highlight(position);
    }

    private void highlight(ChessPosition position) {
        Drawer bd = new Drawer(this);
        bd.highlightValidMoves(position, teamColor);
    }

    private ChessPosition parsePosition(String s) {
        if ("q".equals(s)) {
            return null;
        }

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

        try {
            server.resign(new Resign(gameID, authToken, teamColor));
        } catch (Exception e) {
            printError("Error occurred: " + e.getMessage());
        }
    }

    private void redraw() {
        clearScreen();
    }

    private void move() throws SwitchException {
        if (game.getTeamTurn() != teamColor) {
            printError("It's not your turn");
            return;
        }

        move(parsePosition(promptInput("Start position: ")));
    }

    private void draw() {
        if (game == null) {
            return;
        }
        Drawer bd = new Drawer(this);
        if (game.isGameOver()) {
            ChessGame.TeamColor winner = game.getWinner();
            String msg = "stalemate";
            if (winner != null) {
                if (winner == ChessGame.TeamColor.BLACK) {
                    msg = "Black won";
                } else {
                    msg = "White won";
                }
            }
            print("Game over: " + msg);
        } else if (game.getTeamTurn() == teamColor) {
            print("Your turn");
        } else if (teamColor == null) {
            StringBuilder value = new StringBuilder(game.getTeamTurn().toString().toLowerCase());
            value.replace(0, 1, String.valueOf(Character.toTitleCase(value.charAt(0))));
            print(String.format("%s's turn", value));
        } else {
            print("Waiting for opponent...");
        }
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
                game = message.getGameData().game();
                clearScreen();
            }
            case ERROR -> {
                clearScreen();
                printError(message.getMessage());
            }
            case NOTIFICATION -> {
                clearScreen();
                print(message.getMessage());
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

    @Override
    protected boolean evaluateCommonCommand(String command) throws SwitchException {
        if (exit) {
            exit();
        }
        return super.evaluateCommonCommand(command);
    }

    private void close() {
        server.close();
    }
}
