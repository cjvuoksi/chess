package chess;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final Map<ChessPosition, ChessPiece> chessBoard = new HashMap<>();

    public static final String EMPTY = " \u2003 ";

    public ChessBoard() {
    }

    public ChessBoard(ChessBoard board) {
        for (var entry : board.getBoard().entrySet()) {
            addPiece(new ChessPosition(entry.getKey().getRow(), entry.getKey().getColumn()),
                    new ChessPiece(entry.getValue().getTeamColor(), entry.getValue().getPieceType())); //FIXME
        }
    }

    public void addPiece(ChessPosition position, ChessPiece piece) {
        if (piece == null) {
            chessBoard.remove((position));
            return;
        }
        chessBoard.put(position, piece);
    }

    public ChessPiece getPiece(ChessPosition position) {
        return chessBoard.get(position);
    }

    public void resetBoard() {
        chessBoard.clear();
        //Pawns
        for (int i = 1; i <= 8; i++) {
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
        StringBuilder sb = new StringBuilder("  a   b   c   d   e   f   g   h   \n|");
        for (int rank = 8; rank >= 1; rank--) {
            for (int file = 1; file <= 8; file++) {
                ChessPosition curr = new ChessPosition(rank, file);
                if ((this.getPiece(curr) != null)) {
                    sb.append(this.getPiece(curr));
                } else {
                    sb.append(EMPTY);
                }
                sb.append("|");

            }
            sb.append(rank + 1);
            if (rank > 1) sb.append("\n|");
        }
        return sb.toString();
    }

    public Map<ChessPosition, ChessPiece> getBoard() {
        return chessBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.equals(chessBoard, that.chessBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(chessBoard);
    }
}
