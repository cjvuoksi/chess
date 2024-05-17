package server;

import chess.ChessGame;

public record SessionInfo(int id, ChessGame.TeamColor color) {
}
