package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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

    private TeamColor winner = null;
    private boolean gameOver = false;

    public TeamColor getWinner() {
        return winner;
    }

    public void setWinner(TeamColor winner) {
        this.winner = winner;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public ChessGame() {
        board.resetBoard();
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
            if (!whiteKingMoved && startPosition.equals(new ChessPosition(1, 5))) {
                if (!whiteKingRookMoved) {
                    shortCastle(startPosition, moves, currColor);
                }
                if (!whiteQueenRookMoved) {
                    longCastle(startPosition, moves, currColor);
                }
            }
        }
        else {
            if (!blackKingMoved && startPosition.equals(new ChessPosition(8, 5))) {
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
        ChessPosition rook = new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 1);
        if (isMoveInCheck(new ChessMove(startPosition, rook, null), currColor)) {
            return;
        }

        ChessPosition castle = new ChessPosition(startPosition.getRow(), startPosition.getColumn() + 2);
        if (castle.isValid()) {
            if (board.getPiece(castle) == null) {
                moves.add(new ChessMove(startPosition, castle, null));
            }
        }
    }

    private void enPassantMoves(ChessPosition startPosition, TeamColor color, Collection<ChessMove> moves) {
        int curRank = startPosition.getRow();
        int curFile = startPosition.getColumn();
        int increment = color == TeamColor.WHITE ? 1 : -1;

        ChessPosition attackLeft = new ChessPosition(curRank + increment, curFile - 1);
        ChessPosition passantLeft = new ChessPosition(curRank, curFile - 1);
        if (attackLeft.isValid() && passantLeft.equals(enPassant)) {
            moves.add(new ChessMove(startPosition, attackLeft, null));
        }

        ChessPosition attackRight = new ChessPosition(curRank + increment, curFile + 1);
        ChessPosition passantRight = new ChessPosition(curRank, curFile + 1);
        if (attackRight.isValid() && passantRight.equals(enPassant)) {
            moves.add(new ChessMove(startPosition, attackRight, null));
        }
    }

    private void movePiece(ChessMove move) {
        ChessPiece movePiece = board.getPiece(move.getStartPosition());
        if (movePiece == null) {
            return;
        }

        if (move.getPromotionPiece() == null) {
            if (movePiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                ChessPosition enPassantSquare = new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn());
                if (enPassant != null && enPassant.equals(enPassantSquare)) {
                    board.addPiece(enPassantSquare, null);
                }
            }
            // NB castling does not fully need to be performed as it cannot be performed to escape check or capture
            board.addPiece(move.getEndPosition(), movePiece);
        } else {
            ChessPiece promo = promotionPiece(movePiece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promo);
        }
        board.addPiece(move.getStartPosition(), null);
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (gameOver) {
            switch (winner) {
                case WHITE:
                    throw new InvalidMoveException("Game over : White won");
                case BLACK:
                    throw new InvalidMoveException("Game over : Black won");
                default:
                    throw new InvalidMoveException("Game over : Stalemate");
            }
        }

        ChessPiece movePiece = board.getPiece(move.getStartPosition());
        if (movePiece == null) {
            throw new InvalidMoveException(String.format("The initial square %s has no piece to move",
                    move.getStartPosition().toString()));
        }

        if (movePiece.getTeamColor() != currColor) {
            throw new InvalidMoveException("Attempted to move wrong color");
        }

        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException(String.format("The move %s was invalid", move));
        }

        if (move.getPromotionPiece() == null) {
            if (movePiece.getPieceType() == ChessPiece.PieceType.PAWN) {
                // Checks if pawn move two squares
                if (Math.abs(move.getEndPosition().getRow() - move.getStartPosition().getRow()) > 1) {
                    enPassant = move.getEndPosition();
                } else {
                    captureEnPassant(move);
                }
            }
            if (movePiece.getPieceType() == ChessPiece.PieceType.KING) {
                setKingFlags(currColor);
                castleRook(move);
            }
            if (movePiece.getPieceType() == ChessPiece.PieceType.ROOK) {
                setRookFlags(move, movePiece.getTeamColor());
            }
            board.addPiece(move.getEndPosition(), movePiece);
        } else {
            ChessPiece promoPiece = promotionPiece(movePiece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promoPiece);
        }

        board.addPiece(move.getStartPosition(), null);
        currColor = currColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    private void captureEnPassant(ChessMove move) {
        ChessPosition enSquare = new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn());
        if (enPassant != null && enPassant.equals(enSquare)) {
            board.addPiece(enSquare, null);
        }
        enPassant = null;
    }

    private void setKingFlags(TeamColor color) {
        if (color == TeamColor.WHITE) {
            whiteKingMoved = true;
        } else {
            blackKingMoved = true;
        }
    }

    private void setRookFlags(ChessMove move, TeamColor color) {
        if (color == TeamColor.WHITE) {
            if (move.getStartPosition().getColumn() == 1) {
                whiteQueenRookMoved = true;
            } else if (move.getStartPosition().getColumn() == 8) {
                whiteKingRookMoved = true;
            }
        } else {
            if (move.getStartPosition().getRow() == 1) {
                blackQueenRookMoved = true;
            } else if (move.getStartPosition().getColumn() == 8) {
                blackKingRookMoved = true;
            }
        }
    }

    private void castleRook(ChessMove move) {
        if (isCastle(move)) {
            return;
        }
        if (isLongCastle(move)) {
            moveRookCastle(move, 1, 4);
        } else {
            moveRookCastle(move, 8, 6);
        }
    }

    private boolean isCastle(ChessMove move) {
        return Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) != 2;
    }

    private boolean isLongCastle(ChessMove move) {
        return move.getEndPosition().getColumn() == 3;
    }

    private void moveRookCastle(ChessMove move, int startFile, int endFile) {
        ChessPosition rookStart = new ChessPosition(move.getStartPosition().getRow(), startFile);
        ChessPosition rookEnd = new ChessPosition(move.getStartPosition().getRow(), endFile);
        if (board.getPiece(rookStart).getPieceType() == ChessPiece.PieceType.ROOK) {
            movePiece(new ChessMove(rookStart, rookEnd, null));
        }
    }

    private ChessPiece promotionPiece(TeamColor color, ChessPiece.PieceType type) {
        return new ChessPiece(color, type);
    }

    private boolean isMoveInCheck(ChessMove move, TeamColor color) {
        ChessBoard copy = new ChessBoard(board);
        movePiece(move);
        boolean check = isInCheck(color);
        reset(copy);
        return check;
    }

    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> opposingMoves = checkingMoves(teamColor);
        return !opposingMoves.isEmpty();
    }

    private Collection<ChessMove> checkingMoves(TeamColor teamColor) {
        ChessPosition king = findKing(teamColor);
        Collection<ChessMove> opposingMoves = getOpposingMoves(teamColor);
        HashSet<ChessMove> checkMoves = new HashSet<>();

        for (ChessMove move : opposingMoves) {
            if (move.getEndPosition().equals(king)) {
                checkMoves.add(move);
            }
        }
        return checkMoves;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for (var entry : board.getBoard().entrySet()) {
            if (entry.getValue().getPieceType() == ChessPiece.PieceType.KING &&
                    entry.getValue().getTeamColor() == teamColor) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Collection<ChessMove> getOpposingMoves(TeamColor teamColor) {
        HashSet<ChessMove> opposingMoves = new HashSet<>();

        for (var entry : board.getBoard().entrySet()) {
            if (entry.getValue().getTeamColor() != teamColor) {
                opposingMoves.addAll(entry.getValue().pieceMoves(board, entry.getKey()));
            }
        }

        return opposingMoves;
    }

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
        ChessBoard copy = new ChessBoard(board);

        for (var entry : copy.getBoard().entrySet()) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return whiteKingMoved == chessGame.whiteKingMoved &&
                whiteQueenRookMoved == chessGame.whiteQueenRookMoved &&
                whiteKingRookMoved == chessGame.whiteKingRookMoved &&
                blackKingMoved == chessGame.blackKingMoved &&
                blackQueenRookMoved == chessGame.blackQueenRookMoved &&
                blackKingRookMoved == chessGame.blackKingRookMoved &&
                gameOver == chessGame.gameOver && Objects.equals(board, chessGame.board) &&
                Objects.equals(enPassant, chessGame.enPassant) && currColor == chessGame.currColor &&
                winner == chessGame.winner;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board,
                enPassant,
                whiteKingMoved,
                whiteQueenRookMoved,
                whiteKingRookMoved,
                blackKingMoved,
                blackQueenRookMoved,
                blackKingRookMoved,
                currColor,
                winner,
                gameOver);
    }
}
