package com.example.ChessParadox.Main;

import com.example.ChessParadox.Pieces.Piece;

/**
 * Handles check detection logic for the chess game
 */
public class CheckScanner {
    private final Chessboard chessBoard;

    public CheckScanner(Chessboard chessBoard) {
        this.chessBoard = chessBoard;
    }

    /**
     * Determines if the king of the current player is in check after a move
     */
    public boolean isKingInCheck(Move move) {
        // Find the king of the current player
        Piece king = chessBoard.findKing(move.piece.isWhite);
        if (king == null) return false;

        // Get king position, accounting for if the king is the piece being moved
        int kingCol = king.col;
        int kingRow = king.row;
        if (move.piece.name.equals("King")) {
            kingCol = move.newCol;
            kingRow = move.newRow;
        }

        // Check attacks from each piece type
        return isAttackedByRook(kingCol, kingRow, king, move) ||
                isAttackedByBishop(kingCol, kingRow, king, move) ||
                isAttackedByKnight(kingCol, kingRow, king, move) ||
                isAttackedByPawn(kingCol, kingRow, king, move) ||
                isAttackedByKing(kingCol, kingRow, king, move);
    }

    /**
     * Check if a king is attacked by a rook or queen along ranks or files
     */
    private boolean isAttackedByRook(int kingCol, int kingRow, Piece king, Move move) {
        // Check in four directions: up, right, down, left
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] dir : directions) {
            int colDir = dir[0];
            int rowDir = dir[1];

            for (int i = 1; i < 8; i++) {
                int col = kingCol + (i * colDir);
                int row = kingRow + (i * rowDir);

                // Stop at board edge
                if (col < 0 || col > 7 || row < 0 || row > 7) break;

                // Skip the destination of the current move
                if (col == move.newCol && row == move.newRow) continue;

                Piece piece = chessBoard.getPiece(col, row);
                if (piece != null) {
                    if (!chessBoard.sameTeam(piece, king) &&
                            (piece.name.equals("Rook") || piece.name.equals("Queen"))) {
                        return true;
                    }
                    break; // Blocked by a piece
                }
            }
        }
        return false;
    }

    /**
     * Check if a king is attacked by a bishop or queen along diagonals
     */
    private boolean isAttackedByBishop(int kingCol, int kingRow, Piece king, Move move) {
        // Check in four diagonal directions
        int[][] directions = {{-1, -1}, {1, -1}, {1, 1}, {-1, 1}};

        for (int[] dir : directions) {
            int colDir = dir[0];
            int rowDir = dir[1];

            for (int i = 1; i < 8; i++) {
                int col = kingCol + (i * colDir);
                int row = kingRow + (i * rowDir);

                // Stop at board edge
                if (col < 0 || col > 7 || row < 0 || row > 7) break;

                // Skip the destination of the current move
                if (col == move.newCol && row == move.newRow) continue;

                Piece piece = chessBoard.getPiece(col, row);
                if (piece != null) {
                    if (!chessBoard.sameTeam(piece, king) &&
                            (piece.name.equals("Bishop") || piece.name.equals("Queen"))) {
                        return true;
                    }
                    break; // Blocked by a piece
                }
            }
        }
        return false;
    }

    /**
     * Check if a king is attacked by a knight
     */
    private boolean isAttackedByKnight(int kingCol, int kingRow, Piece king, Move move) {
        int[][] knightMoves = {
                {-1, -2}, {1, -2}, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}
        };

        for (int[] km : knightMoves) {
            int col = kingCol + km[0];
            int row = kingRow + km[1];

            // Skip if off board or is the current move destination
            if (col < 0 || col > 7 || row < 0 || row > 7 ||
                    (col == move.newCol && row == move.newRow)) continue;

            Piece piece = chessBoard.getPiece(col, row);
            if (piece != null && !chessBoard.sameTeam(piece, king) && piece.name.equals("Knight")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a king is attacked by a pawn
     */
    private boolean isAttackedByPawn(int kingCol, int kingRow, Piece king, Move move) {
        int colorVal = king.isWhite ? -1 : 1;

        // Check the two diagonal capture positions for pawns
        int[][] pawnCaptures = {{1, colorVal}, {-1, colorVal}};

        for (int[] pc : pawnCaptures) {
            int col = kingCol + pc[0];
            int row = kingRow + pc[1];

            // Skip if off board or is the current move destination
            if (col < 0 || col > 7 || row < 0 || row > 7 ||
                    (col == move.newCol && row == move.newRow)) continue;

            Piece piece = chessBoard.getPiece(col, row);
            if (piece != null && !chessBoard.sameTeam(piece, king) && piece.name.equals("Pawn")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a king is attacked by the opponent's king
     */
    private boolean isAttackedByKing(int kingCol, int kingRow, Piece king, Move move) {
        // Check all 8 squares around the king
        for (int r = -1; r <= 1; r++) {
            for (int c = -1; c <= 1; c++) {
                if (r == 0 && c == 0) continue; // Skip the king's own position

                int col = kingCol + c;
                int row = kingRow + r;

                // Skip if off board or is the current move destination
                if (col < 0 || col > 7 || row < 0 || row > 7 ||
                        (col == move.newCol && row == move.newRow)) continue;

                Piece piece = chessBoard.getPiece(col, row);
                if (piece != null && !chessBoard.sameTeam(piece, king) && piece.name.equals("King")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the game is over (checkmate or stalemate)
     */
    public boolean isGameOver(Piece king) {
        // Check if any piece of the same color as the king can make a valid move
        for (Piece piece : chessBoard.pieceList) {
            if (chessBoard.sameTeam(piece, king)) {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        Move move = new Move(chessBoard, piece, col, row);
                        if (chessBoard.isValidMove(move)) {
                            return false; // Valid move found, game is not over
                        }
                    }
                }
            }
        }
        return true; // No valid moves, game is over
    }
}