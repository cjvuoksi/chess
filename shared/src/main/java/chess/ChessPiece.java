package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static chess.ChessGame.*;
import static chess.ChessGame.TeamColor.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final PieceType type;
    private final TeamColor color;

    public ChessPiece(ChessGame.TeamColor color, ChessPiece.PieceType type) {
        this.type = type;
        this.color = color;
    }

    private enum Multiplier {
        /**
         * Indicates that movement increments in the positive direction
         */
        UP,
        /**
         * Indicates that movement increments in the negative direction
         */
        DOWN,
        /**
         * Indicates no movement
         */
        NONE
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessGame.TeamColor currColor = this.getTeamColor();
        int curRank = myPosition.getRow();
        int curFile = myPosition.getColumn();
        switch (type) {
            case KING:
                for (curRank = myPosition.getRow() + 1; curRank >= myPosition.getRow() - 1; curRank--) {
                    for (curFile = myPosition.getColumn() + 1; curFile >= myPosition.getColumn() - 1; curFile--) {
                        if (!(curFile == myPosition.getColumn() && curRank == myPosition.getRow())) {
                            ChessPosition square = new ChessPosition(curRank, curFile);
                            if (square.isValid()) { //If the square is on the board
                                if (board.getPiece(square) == null) {
                                    moves.add(new ChessMove(myPosition, square, null));
                                } else if (board.getPiece(square).getTeamColor() != currColor) {
                                    moves.add(new ChessMove(myPosition, square, null));
                                }
                            }
                        }
                    }
                }
                break;
            case QUEEN:
                moves.addAll(axialMoves(board, myPosition));
            case BISHOP:
                moves.addAll(diagonalMoves(board, myPosition));
                break;
            case KNIGHT:
                int[] cycle = {2, 2, 1, 1, -2, -2, -1, -1};

                for (int i = 0; i < cycle.length; i++) {
                    ChessPosition end = new ChessPosition(curRank + cycle[i], curFile + cycle[(i * 3 + 3) % 8]);
                    if (end.isValid()) {
                        if (board.getPiece(end) == null) {
                            moves.add(new ChessMove(myPosition, end, null));
                        } else if (board.getPiece(end).getTeamColor() != currColor) {
                            moves.add(new ChessMove(myPosition, end, null));
                        }
                    }
                }
                break;
            case ROOK:
                moves.addAll(axialMoves(board, myPosition));
                break;
            case PAWN:
                int increment = currColor == ChessGame.TeamColor.WHITE ? 1 : -1;
                int promo = currColor == ChessGame.TeamColor.WHITE ? 8 : 1;
                setForwardMoves(board, myPosition, increment, promo, moves);

                ChessPosition attackLeft = new ChessPosition(curRank + increment, curFile - 1);
                setAttackMoves(board, myPosition, moves, curRank, currColor, increment, promo, attackLeft);

                ChessPosition attackRight = new ChessPosition(curRank + increment, curFile + 1);
                setAttackMoves(board, myPosition, moves, curRank, currColor, increment, promo, attackRight);
        }
        return moves;
    }

    private void setAttackMoves(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> pawnMoves, int curRank, ChessGame.TeamColor curColor, int increment, int promo, ChessPosition attack) {
        if (attack.isValid()) {
            if (board.getPiece(attack) != null && board.getPiece(attack).getTeamColor() != curColor) {
                if ((curRank + increment) == promo) {
                    pawnMoves.addAll(getPawnPromotions(myPosition, attack));
                } else {
                    pawnMoves.add(new ChessMove(myPosition, attack, null));
                }
            }
        }
    }

    private void setForwardMoves(ChessBoard board, ChessPosition myPosition, int increment, int promoRank, HashSet<ChessMove> pawnMoves) {
        ChessPosition forward = new ChessPosition(myPosition.getRow() + increment, myPosition.getColumn());
        if (!forward.isValid() || board.getPiece(forward) != null) {
            return;
        }

        if ((myPosition.getRow() + increment) == promoRank) {
            pawnMoves.addAll(getPawnPromotions(myPosition, forward));
        } else {
            pawnMoves.add(new ChessMove(myPosition, forward, null));
        }

        if ((myPosition.getRow() == 2 && this.color == ChessGame.TeamColor.WHITE) ||
                (myPosition.getRow() == 7 && this.color == ChessGame.TeamColor.BLACK)) {
            ChessPosition twoForward = new ChessPosition(myPosition.getRow() + 2 * increment, myPosition.getColumn());
            if (twoForward.isValid() && board.getPiece(twoForward) == null) {
                pawnMoves.add(new ChessMove(myPosition, twoForward, null));
            }
        }
    }

    private Collection<ChessMove> getPawnPromotions(ChessPosition start, ChessPosition end) {
        HashSet<ChessMove> promotions = new HashSet<>();
        promotions.add(new ChessMove(start, end, PieceType.QUEEN));
        promotions.add(new ChessMove(start, end, PieceType.ROOK));
        promotions.add(new ChessMove(start, end, PieceType.BISHOP));
        promotions.add(new ChessMove(start, end, PieceType.KNIGHT));
        return promotions;
    }

    protected Collection<ChessMove> axialMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> results = new HashSet<>();
        results.addAll(linear(board, myPosition, Multiplier.UP, Multiplier.NONE)); //Adds upward moves
        results.addAll(linear(board, myPosition, Multiplier.DOWN, Multiplier.NONE)); //Adds downward moves
        results.addAll(linear(board, myPosition, Multiplier.NONE, Multiplier.DOWN)); //Adds left moves
        results.addAll(linear(board, myPosition, Multiplier.NONE, Multiplier.UP)); //Adds right moves
        return results;
    }

    protected Collection<ChessMove> diagonalMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> results = new HashSet<>();
        results.addAll(linear(board, myPosition, Multiplier.UP, Multiplier.UP)); //Adds NE moves
        results.addAll(linear(board, myPosition, Multiplier.UP, Multiplier.DOWN)); //Adds NW moves
        results.addAll(linear(board, myPosition, Multiplier.DOWN, Multiplier.UP)); //Adds SE moves
        results.addAll(linear(board, myPosition, Multiplier.DOWN, Multiplier.DOWN)); //Adds SW moves
        return results;
    }

    private Collection<ChessMove> linear(ChessBoard board, ChessPosition myPosition, Multiplier directionVertical, Multiplier directionHorizontal) {
        HashSet<ChessMove> linear = new HashSet<>();
        ChessGame.TeamColor currColor = board.getPiece(myPosition).getTeamColor();
        int multiplierVertical = directionVertical == Multiplier.NONE ? 0 : (directionVertical == Multiplier.UP ? 1 : -1);
        int multiplierHorizontal = directionHorizontal == Multiplier.NONE ? 0 : (directionHorizontal == Multiplier.UP ? 1 : -1);

        int curRank = myPosition.getRow() + multiplierVertical;
        int curFile = myPosition.getColumn() + multiplierHorizontal;

        while (curRank >= 1 && curRank <= 8 && curFile >= 1 && curFile <= 8) {
            ChessPosition square = new ChessPosition(curRank, curFile);
            if (board.getPiece(square) == null) {
                linear.add(new ChessMove(myPosition, square, null));
                curRank = curRank + multiplierVertical;
                curFile = curFile + multiplierHorizontal;
                continue;
            }
            if (board.getPiece(square).getTeamColor() != currColor) {
                linear.add(new ChessMove(myPosition, square, null));
            }
            break;
        }
        return linear;
    }

    @java.lang.Override
    public java.lang.String toString() {
         switch (type) {
            case KING:
                return color == WHITE ? "♔" : "♚";
            case PAWN:
                return color == WHITE ? "♙" : "♟";
            case ROOK:
                return color == WHITE ? "♖" : "♜";
            case QUEEN:
                return color == WHITE ? "♕" : "♛";
            case BISHOP:
                return color == WHITE ? "♗" : "♝";
            case KNIGHT:
                return color == WHITE ? "♘" : "♞";
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return type == that.type && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }
}
