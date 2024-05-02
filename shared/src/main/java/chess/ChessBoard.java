package chess;

import java.util.HashMap;
import java.util.Map;
import static chess.ChessGame.TeamColor.*;
import static chess.ChessPiece.PieceType.*;

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
        return board.get(position);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board.clear();
        //Pawns
        for (int i = 1; i < 8; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(WHITE, PAWN));
            addPiece(new ChessPosition(7, i), new ChessPiece(BLACK, PAWN));
        }

        // Rooks
        addPiece(new ChessPosition(1,1), new ChessPiece(WHITE, ROOK));
        addPiece(new ChessPosition(1,8), new ChessPiece(WHITE, ROOK));
        addPiece(new ChessPosition(8,1), new ChessPiece(BLACK, ROOK));
        addPiece(new ChessPosition(8,8), new ChessPiece(BLACK, ROOK));

        // Knights
        addPiece(new ChessPosition(1,2), new ChessPiece(WHITE, KNIGHT));
        addPiece(new ChessPosition(1,7), new ChessPiece(WHITE, KNIGHT));
        addPiece(new ChessPosition(8,2), new ChessPiece(BLACK, KNIGHT));
        addPiece(new ChessPosition(8,7), new ChessPiece(BLACK, KNIGHT));

        // Bishops
        addPiece(new ChessPosition(1,3), new ChessPiece(WHITE, BISHOP));
        addPiece(new ChessPosition(1,6), new ChessPiece(WHITE, BISHOP));
        addPiece(new ChessPosition(8,3), new ChessPiece(BLACK, BISHOP));
        addPiece(new ChessPosition(8,6), new ChessPiece(BLACK, BISHOP));

        // Royals
        addPiece(new ChessPosition(1,4), new ChessPiece(WHITE, QUEEN));
        addPiece(new ChessPosition(1,5), new ChessPiece(WHITE, KING));
        addPiece(new ChessPosition(8,4), new ChessPiece(BLACK, QUEEN));
        addPiece(new ChessPosition(8,5), new ChessPiece(BLACK, KING));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(" a b c d e f g h \n|");
        for (int rank = 8; rank >= 1; rank--) {
            for (int file = 1; file < 8; file++) {
                ChessPiece curr = getPiece(new ChessPosition(rank, file));
                if (curr != null) {
                    sb.append(curr);
                } else {
                    sb.append(" ");
                }
                sb.append("|");

            }
            sb.append(rank);
            if (rank > 1) sb.append("\n|");
        }
        return sb.toString();
    }
}
