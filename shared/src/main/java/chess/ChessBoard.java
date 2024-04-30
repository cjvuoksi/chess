package chess;

import java.util.HashMap;
import java.util.Map;
import static chess.ChessGame.TeamColor;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final Map<ChessPosition, ChessPiece> board = new HashMap<>();


    public ChessBoard() {
        resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board.put(position, piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        board.get(position);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board.clear();

        for (int i = 1; i < 8; i++) {
            ChessPosition white_pawn = new ChessPosition(2, i);
            ChessPosition black_pawn = new ChessPosition(7, i);
            addPiece(white_pawn, new ChessPiece(WHITE, white_pawn));
            addPiece(black_pawn, new ChessPiece(BLACK, black_pawn));
        }


    }
}
