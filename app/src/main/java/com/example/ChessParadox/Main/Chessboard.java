package com.example.ChessParadox.Main;

import com.example.ChessParadox.Pieces.Piece;
import java.util.ArrayList;

/**
 * Represents the chess board and manages game rules
 */
public class Chessboard {
    public Chessboardview view;
    public int tileSize;
    public ArrayList<Piece> pieceList = new ArrayList<>();
    public Piece selectedPiece;
    public int enPassantTile = -1;
    private CheckScanner checkScanner;

    public Chessboard(Chessboardview view) {
        this.view = view;
        this.tileSize = view != null ? view.getTileSize() : 100;
    }

    /**
     * Initialize the check scanner after the board is fully created
     */
    public void initCheckScanner() {
        this.checkScanner = new CheckScanner(this);
    }

    /**
     * Get the check scanner
     */
    public CheckScanner getCheckScanner() {
        if (checkScanner == null) {
            initCheckScanner();
        }
        return checkScanner;
    }

    /**
     * Calculate tile number for en passant detection
     */
    public int getTileNum(int col, int row) {
        return row * 8 + col;
    }

    /**
     * Determine if a move is valid according to chess rules
     */
    public boolean isValidMove(Move move) {
        if (move == null || move.piece == null) {
            return false;
        }

        // Check if move is within board boundaries
        if (move.newCol < 0 || move.newCol > 7 || move.newRow < 0 || move.newRow > 7) {
            return false;
        }

        // Can't move to the same position
        if (move.newCol == move.piece.col && move.newRow == move.piece.row) {
            return false;
        }

        // Check piece-specific movement pattern
        if (!move.piece.isValidMovement(move.newCol, move.newRow)) {
            return false;
        }

        // Check if the move path is obstructed
        if (move.piece.moveCollidesWithPiece(move.newCol, move.newRow)) {
            return false;
        }

        // Can't capture own pieces
        Piece pieceAtDestination = getPiece(move.newCol, move.newRow);
        if (pieceAtDestination != null && sameTeam(move.piece, pieceAtDestination)) {
            return false;
        }

        // Special case for king to prevent moving into check
        if (move.piece.name.equals("King")) {
            // Handle castling
            if (Math.abs(move.piece.col - move.newCol) > 1) {
                return isCastlingValid(move);
            }
        }

        // Special case for en passant with pawns
        if (move.piece.name.equals("Pawn")) {
            // Handle en passant capture
            if (move.piece.col != move.newCol && getPiece(move.newCol, move.newRow) == null) {
                return isEnPassantValid(move);
            }
        }

        // Check if this move would put/leave the king in check
        return !wouldBeInCheck(move);
    }

    /**
     * Determine if castling is valid
     */
    private boolean isCastlingValid(Move move) {
        int row = move.piece.row;

        // Check that king and rook haven't moved
        if (!move.piece.isFirstMove) {
            return false;
        }

        // Kingside castling
        if (move.newCol == 6) {
            Piece rook = getPiece(7, row);
            if (rook == null || !rook.name.equals("Rook") || !rook.isFirstMove) {
                return false;
            }

            // Check if path is clear
            if (getPiece(5, row) != null || getPiece(6, row) != null) {
                return false;
            }

            // Check if king passes through check
            if (wouldBeInCheck(new Move(this, move.piece, 5, row))) {
                return false;
            }
        }
        // Queenside castling
        else if (move.newCol == 2) {
            Piece rook = getPiece(0, row);
            if (rook == null || !rook.name.equals("Rook") || !rook.isFirstMove) {
                return false;
            }

            // Check if path is clear
            if (getPiece(1, row) != null || getPiece(2, row) != null || getPiece(3, row) != null) {
                return false;
            }

            // Check if king passes through check
            if (wouldBeInCheck(new Move(this, move.piece, 3, row))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determine if en passant capture is valid
     */
    private boolean isEnPassantValid(Move move) {
        int colorVal = move.piece.isWhite ? -1 : 1;

        // Check if the target square is the recorded en passant tile
        if (getTileNum(move.newCol, move.newRow) == enPassantTile) {
            // Check if there's an enemy pawn in the adjacent square
            Piece pawnToCapture = getPiece(move.newCol, move.newRow + colorVal);
            return pawnToCapture != null &&
                    pawnToCapture.name.equals("Pawn") &&
                    !sameTeam(move.piece, pawnToCapture);
        }

        return false;
    }

    /**
     * Check if a move would put/leave the king in check
     */
    private boolean wouldBeInCheck(Move move) {
        // Save the current state
        Piece originalPiece = move.piece;
        int originalCol = originalPiece.col;
        int originalRow = originalPiece.row;

        // Temporarily make the move
        originalPiece.col = move.newCol;
        originalPiece.row = move.newRow;

        // Store the captured piece (if any)
        Piece capturedPiece = getPiece(move.newCol, move.newRow);
        if (capturedPiece != null) {
            pieceList.remove(capturedPiece);
        }

        // Check if the king is in check after the move
        boolean inCheck = getCheckScanner().isKingInCheck(move);

        // Restore the original state
        originalPiece.col = originalCol;
        originalPiece.row = originalRow;
        if (capturedPiece != null) {
            pieceList.add(capturedPiece);
        }

        return inCheck;
    }

    /**
     * Find the king of a given color
     */
    public Piece findKing(boolean isWhite) {
        for (Piece piece : pieceList) {
            if (isWhite == piece.isWhite && piece.name.equals("King")) {
                return piece;
            }
        }
        return null;
    }

    /**
     * Check if two pieces are on the same team
     */
    public boolean sameTeam(Piece p1, Piece p2) {
        if (p1 == null || p2 == null) {
            return false;
        }
        return p1.isWhite == p2.isWhite;
    }

    /**
     * Get a piece at a specific position
     */
    public Piece getPiece(int col, int row) {
        if (col < 0 || col > 7 || row < 0 || row > 7) {
            return null;
        }

        for (Piece piece : pieceList) {
            if (piece != null && piece.col == col && piece.row == row) {
                return piece;
            }
        }
        return null;
    }

    /**
     * Make a move on the board
     */
    public void makeMove(Move move) {
        // Save the old position
        int oldCol = move.piece.col;
        int oldRow = move.piece.row;

        // Update en passant opportunities
        if (move.piece.name.equals("Pawn") && Math.abs(move.piece.row - move.newRow) == 2) {
            // Set en passant square behind the pawn
            int direction = move.piece.isWhite ? 1 : -1;
            enPassantTile = getTileNum(move.newCol, move.newRow + direction);
        } else {
            enPassantTile = -1; // Reset en passant
        }

        // Handle en passant capture
        if (move.piece.name.equals("Pawn") && move.piece.col != move.newCol &&
                getPiece(move.newCol, move.newRow) == null) {
            // Remove the captured pawn
            int captureRow = move.piece.row;
            Piece capturedPawn = getPiece(move.newCol, captureRow);
            if (capturedPawn != null) {
                pieceList.remove(capturedPawn);
            }
        }

        // Handle castling
        if (move.piece.name.equals("King") && Math.abs(move.piece.col - move.newCol) > 1) {
            // Kingside castling
            if (move.newCol == 6) {
                Piece rook = getPiece(7, move.piece.row);
                if (rook != null) {
                    rook.col = 5;
                    rook.isFirstMove = false;
                }
            }
            // Queenside castling
            else if (move.newCol == 2) {
                Piece rook = getPiece(0, move.piece.row);
                if (rook != null) {
                    rook.col = 3;
                    rook.isFirstMove = false;
                }
            }
        }

        // Capture a piece if there is one at the destination
        Piece capturedPiece = getPiece(move.newCol, move.newRow);
        if (capturedPiece != null) {
            pieceList.remove(capturedPiece);
        }

        // Move the piece
        move.piece.col = move.newCol;
        move.piece.row = move.newRow;
        move.piece.isFirstMove = false;

        // Check for pawn promotion
        if (move.piece.name.equals("Pawn")) {
            checkPawnPromotion(move.piece);
        }
    }

    /**
     * Check for pawn promotion when a pawn reaches the opposite end of the board
     */
    private void checkPawnPromotion(Piece pawn) {
        // Check if pawn has reached the opposite end
        if ((pawn.isWhite && pawn.row == 0) || (!pawn.isWhite && pawn.row == 7)) {
            // For simplicity, auto-promote to Queen
            // In a full implementation, you'd show a dialog to let the player choose
            promotePawn(pawn, "Queen");
        }
    }

    /**
     * Promote a pawn to another piece
     */
    private void promotePawn(Piece pawn, String newPieceName) {
        // This would be implemented with factory method to create the new piece
        // For now, just change the name for simplicity
        pawn.name = newPieceName;
    }
}