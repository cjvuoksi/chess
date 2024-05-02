package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board = new ChessBoard();

    private ChessPosition enPassant;
    private boolean whiteKingMoved = false;
    private boolean whiteQueenRookMoved = false;
    private boolean whiteKingRookMoved = false;
    private boolean blackKingMoved = false;
    private boolean blackQueenRookMoved = false;
    private boolean blackKingRookMoved = false;

    private TeamColor currColor = TeamColor.WHITE;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currPiece = board.getPiece(startPosition);
        if (currPiece == null) {
            return null;
        }

        Collection<ChessMove> moves;
        TeamColor color = currPiece.getTeamColor();
        moves = currPiece.pieceMoves(board, startPosition);

        if (currPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            enPassantMoves(startPosition, color, moves);
        }

        if (currPiece.getPieceType() == ChessPiece.PieceType.KING) {
            castlingMoves(startPosition, color, moves);
        }

        HashSet<ChessMove> deletes = new HashSet<>();
        for (ChessMove move : moves) {
            if (isMoveInCheck(move, color)) {
                deletes.add(move);
            }
        }
        moves.removeAll(deletes);

        return moves;
    }

    private void castlingMoves(ChessPosition startPosition, TeamColor color, Collection<ChessMove> moves) {
        if (isInCheck(color)) {
            return;
        }

        if (color == TeamColor.WHITE) {
            if (!whiteKingMoved && startPosition.equals(new ChessPosition(0, 4))) {
                if (!whiteKingRookMoved) {
                    shortCastle(startPosition, moves, currColor);
                }
                if (!whiteQueenRookMoved) {
                    longCastle(startPosition, moves, currColor);
                }
            }
        }
        else {
            if (!blackKingMoved && startPosition.equals(new ChessPosition(7, 4))) {
                if (!blackKingRookMoved) {
                    shortCastle(startPosition, moves, color);
                }
                if (!blackQueenRookMoved) {
                    longCastle(startPosition, moves, color);
                }
            }
        }

    }

    private void longCastle(ChessPosition startPosition, Collection<ChessMove> moves, TeamColor currColor) {
        ChessPosition rook = new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 1);
        if (isMoveInCheck(new ChessMove(startPosition, rook, null), currColor) || board.getPiece(rook) != null) {
            return;
        }

        ChessPosition castle = new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 2);
        ChessPosition longCastle = new ChessPosition(startPosition.getRow(), startPosition.getColumn() - 3);

        if (castle.isValid() && longCastle.isValid()) {
            if (board.getPiece(castle) == null && board.getPiece(longCastle) == null) {
                moves.add(new ChessMove(startPosition, castle, null));
            }
        }
    }

    private void shortCastle(ChessPosition startPosition, Collection<ChessMove> moves, TeamColor currColor) {
        ChessPosition rook = new Position(startPosition.getRow(), startPosition.getColumn() + 1);
        if (isMoveInCheck(new ChessMove(startPosition, rook, null), currColor)) {
            return;
        }

        ChessPosition castle = new Position(startPosition.getRow(), startPosition.getColumn() + 2);
        if (castle.isValid()) {
            if (gameBoard.getPiece(castle) == null) {
                moves.add(new ChessMove(startPosition, castle));
            }
        }
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return allValid(teamColor).isEmpty();
        } else {
            return false;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return allValid(teamColor).isEmpty();
        } else {
            return false;
        }
    }

    private Collection<ChessMove> allValid(TeamColor teamColor) {
        HashSet<ChessMove> moves = new HashSet<>();
        for (var entry : board.getBoard().entrySet()) {
            if (teamColor == entry.getValue().getTeamColor()) {
                moves.addAll(validMoves(entry.getKey()));
            }
        }
        return moves;
    }

    public void setBoard(ChessBoard board) {
        whiteKingMoved = false;
        whiteQueenRookMoved = false;
        whiteKingRookMoved = false;
        blackKingMoved = false;
        blackQueenRookMoved = false;
        blackKingRookMoved = false;

        this.board = board;
    }

    private void reset(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
