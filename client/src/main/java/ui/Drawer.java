package ui;

import chess.*;

import java.util.Collection;

/**
 * Prints a chess board for an instance of gameplay
 */
public class Drawer {

    /**
     * The gameplay interface to draw the board to
     */
    private final GameUI ui;

    /**
     * The chess game to be drawn
     */
    private ChessGame game;

    /**
     * Constructs a board printer attached to the given gameplay ui
     *
     * @param ui game play ui to draw
     */
    Drawer(GameUI ui) {
        this.ui = ui;
        game = ui.game;
    }

    public void printBoard(ChessGame.TeamColor color) {
        game = ui.game;
        printBoard(color, getChessBoardArray());
    }

    String[][] getChessBoardArray() {
        ChessBoard board = game.getBoard();
        String[][] chessBoard = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i + 1, j + 1));
                if (piece == null) {
                    chessBoard[i][j] = EscapeSequences.EMPTY;
                } else {
                    chessBoard[i][j] = getPieceCharacter(piece);
                }
            }
        }
        return chessBoard;
    }

    public record LoopParams(int increment, int start, int end) {
    }

    private void printBoard(ChessGame.TeamColor color, String[][] chessBoard) {
        if (color == null) {
            color = ChessGame.TeamColor.WHITE;
        }

        String background = EscapeSequences.DEFAULT;

        LoopParams loop = new LoopParams(1, 0, 7);
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            loop = new LoopParams(-1, 7, 0);
        }

        ui.print(EscapeSequences.SET_TEXT_BOLD);
        ui.print(getRankLabels(color));
        for (int i = loop.start(); i != loop.end() + loop.increment(); i += loop.increment()) {
            printRow(chessBoard, i, color, background);
            background = toggleBackground(background);
        }
        ui.print(getRankLabels(color));
        ui.print(EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

    private void printRow(String[][] chessBoard, int row, ChessGame.TeamColor color, String background) {
        LoopParams loop = new LoopParams(1, 0, 7);
        if (color.equals(ChessGame.TeamColor.BLACK)) {
            loop = new LoopParams(-1, 7, 0);
        }

        StringBuilder rowBuilder = new StringBuilder();
        rowBuilder.append(" ").append(row + 1).append(" ");
        for (int i = loop.start(); i != loop.end() + loop.increment(); i += loop.increment()) {
            rowBuilder.append(background);
            rowBuilder.append(chessBoard[row][i]);
            background = toggleBackground(background);
        }
        rowBuilder.append(EscapeSequences.DEFAULT);
        rowBuilder.append(" ").append(row + 1).append(" ");

        ui.print(rowBuilder.toString());
    }

    String getRankLabels(ChessGame.TeamColor color) {
        StringBuilder rankBuilder = new StringBuilder();

        if (color == ChessGame.TeamColor.BLACK) {
            rankBuilder.reverse();
        }

        for (char c = 'a'; c <= 'h'; c++) {
            rankBuilder.append(" ").append(c).append(" ");
        }

        if (color == ChessGame.TeamColor.BLACK) {
            rankBuilder.reverse();
        }

        //Pads the labels
        rankBuilder.insert(0, EscapeSequences.EMPTY);
        rankBuilder.insert(0, EscapeSequences.DEFAULT);
        rankBuilder.append(EscapeSequences.EMPTY);

        return rankBuilder.toString();
    }

    String getPieceCharacter(ChessPiece piece) {
        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        switch (piece.getPieceType()) {
            case ROOK -> {
                return isWhite ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            }
            case PAWN -> {
                return isWhite ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            }
            case QUEEN -> {
                return isWhite ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            }
            case KING -> {
                return isWhite ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            }
            case BISHOP -> {
                return isWhite ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            }
            case KNIGHT -> {
                return isWhite ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            }
            default -> throw new IllegalStateException("Unexpected value: " + piece.getPieceType());
        }
    }

    private static String toggleBackground(String background) {
        background = background.equals(EscapeSequences.DEFAULT) ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.DEFAULT;
        return background;
    }

    public void highlightValidMoves(ChessPosition position, ChessGame.TeamColor playerColor) {
        highlightValidMoves(position, game.validMoves(position), playerColor);
    }

    private void highlightValidMoves(ChessPosition position, Collection<ChessMove> movesToHighlight, ChessGame.TeamColor playerColor) {
        String[][] chessBoard = getChessBoardArray();

        for (ChessMove move : movesToHighlight) {
            ChessPosition end = move.getEndPosition();

            String highlight = getHighlightColor(end);

            chessBoard[end.getRow()][end.getColumn()] = highlight + chessBoard[end.getRow()][end.getColumn()];
        }

        chessBoard[position.getRow()][position.getColumn()] = EscapeSequences.SET_TEXT_BLINKING + chessBoard[position.getRow()][position.getColumn()] + EscapeSequences.RESET_TEXT_BLINKING;

        ui.print(EscapeSequences.ERASE_SCREEN);
        printBoard(playerColor, chessBoard);
    }

    private String getHighlightColor(ChessPosition position) {
        String highlight = EscapeSequences.SET_BG_COLOR_DARKER_BLUE;
        if (isEven(position.getColumn()) ^ isEven(position.getRow())) {
            highlight = EscapeSequences.SET_BG_COLOR_LIGHT_BLUE;
        }
        return highlight;
    }

    private boolean isEven(int value) {
        return value % 2 == 0;
    }
}