package com.example.ChessParadox.Classic;

import com.example.ChessParadox.Pieces.Bishop;
import com.example.ChessParadox.Pieces.Knight;
import com.example.ChessParadox.Pieces.Pawn;
import com.example.ChessParadox.Pieces.Piece;
import com.example.ChessParadox.Pieces.Queen;
import com.example.ChessParadox.Pieces.Rook;

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

    // Reference to captured pieces views
    private CapturedPiecesView capturedPiecesViewTop;
    private CapturedPiecesView capturedPiecesViewBottom;

    public Chessboard(Chessboardview view) {
        this.view = view;
        this.tileSize = view != null ? view.getTileSize() : 100;
    }

    /**
     * Set references to the captured pieces views
     */
    public void setCapturedPiecesViews(CapturedPiecesView top, CapturedPiecesView bottom) {
        this.capturedPiecesViewTop = top;
        this.capturedPiecesViewBottom = bottom;

        // Set the piece size based on the board's tile size
        if (top != null) {
            top.setPieceSize(tileSize);
        }
        if (bottom != null) {
            bottom.setPieceSize(tileSize);
        }
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

            // Check if king is in check
            if (wouldBeInCheck(new Move(this, move.piece, move.piece.col, move.piece.row))) {
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

            // Check if king is in check
            if (wouldBeInCheck(new Move(this, move.piece, move.piece.col, move.piece.row))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if a move would put/leave the king in check
     */
    private boolean wouldBeInCheck(Move move) {
        // Save the current state
        Piece originalPiece = move.piece;
        int originalCol = originalPiece.col;
        int originalRow = originalPiece.row;

        // Keep track of captured pieces to restore later
        Piece capturedPiece = null;
        Piece enPassantCapturedPiece = null;
        int originalEnPassantTile = enPassantTile;  // Save original en passant state

        // Save all pawns' justMovedTwoSquares state
        boolean[] pawnTwoSquareStates = new boolean[pieceList.size()];
        int pawnIndex = 0;
        for (Piece piece : pieceList) {
            if (piece.name.equals("Pawn") && piece instanceof Pawn) {
                pawnTwoSquareStates[pawnIndex++] = ((Pawn)piece).justMovedTwoSquares;
            }
        }

        try {
            // Check if this is an en passant capture
            if (originalPiece.name.equals("Pawn") &&
                    originalCol != move.newCol &&
                    getPiece(move.newCol, move.newRow) == null &&
                    getTileNum(move.newCol, move.newRow) == enPassantTile) {

                // For en passant, capture the pawn on the same row as the capturing pawn
                enPassantCapturedPiece = getPiece(move.newCol, originalRow);
                if (enPassantCapturedPiece != null) {
                    pieceList.remove(enPassantCapturedPiece);
                }
            }

            // Handle normal capture
            capturedPiece = getPiece(move.newCol, move.newRow);
            if (capturedPiece != null) {
                pieceList.remove(capturedPiece);
            }

            // Temporarily move the piece
            originalPiece.col = move.newCol;
            originalPiece.row = move.newRow;

            // Update en passant opportunity for the simulation
            // If this is a pawn moving two squares, set the en passant tile
            if (originalPiece.name.equals("Pawn") && Math.abs(move.newRow - originalRow) == 2) {
                int direction = originalPiece.isWhite ? -1 : 1;
                enPassantTile = getTileNum(move.newCol, move.newRow + direction);
                if (originalPiece instanceof Pawn) {
                    ((Pawn)originalPiece).justMovedTwoSquares = true;
                }
            } else {
                // Reset en passant opportunity for any other move
                enPassantTile = -1;
                // Reset all other pawns' justMovedTwoSquares flag
                for (Piece piece : pieceList) {
                    if (piece.name.equals("Pawn") && piece instanceof Pawn) {
                        ((Pawn)piece).justMovedTwoSquares = false;
                    }
                }
            }

            // Find the king of the same color as the moving piece
            Piece king = findKing(originalPiece.isWhite);

            // Check if the king is in check after the move
            boolean inCheck = false;
            if (king != null) {
                Move kingMove = new Move(this, king, king.col, king.row);
                inCheck = getCheckScanner().isKingInCheck(kingMove);
            }

            return inCheck;
        } finally {
            // Restore the original state no matter what
            originalPiece.col = originalCol;
            originalPiece.row = originalRow;
            enPassantTile = originalEnPassantTile;  // Restore original en passant state

            // Restore all pawns' justMovedTwoSquares state
            pawnIndex = 0;
            for (Piece piece : pieceList) {
                if (piece.name.equals("Pawn") && piece instanceof Pawn) {
                    if (pawnIndex < pawnTwoSquareStates.length) {
                        ((Pawn)piece).justMovedTwoSquares = pawnTwoSquareStates[pawnIndex++];
                    }
                }
            }

            // Add back any captured pieces
            if (capturedPiece != null && !pieceList.contains(capturedPiece)) {
                pieceList.add(capturedPiece);
            }

            // Add back en passant captured pawn if it was removed
            if (enPassantCapturedPiece != null && !pieceList.contains(enPassantCapturedPiece)) {
                pieceList.add(enPassantCapturedPiece);
            }
        }
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
     * Add a captured piece to the appropriate view
     */
    private void addCapturedPiece(Piece piece, boolean capturedByWhite) {
        if (capturedByWhite) {
            if (capturedPiecesViewBottom != null) {
                capturedPiecesViewBottom.addCapturedPiece(piece, true);
            }
        } else {
            if (capturedPiecesViewTop != null) {
                capturedPiecesViewTop.addCapturedPiece(piece, false);
            }
        }
    }

    /**
     * Make a move on the board
     */
    public void makeMove(Move move) {
        // Save the old position
        int oldCol = move.piece.col;
        int oldRow = move.piece.row;

        // Reset "justMovedTwoSquares" flag for all pawns before making a new move
        for (Piece piece : pieceList) {
            if (piece.name.equals("Pawn") && piece instanceof Pawn) {
                ((Pawn)piece).justMovedTwoSquares = false;
            }
        }

        // Handle en passant capture
        if (move.piece.name.equals("Pawn") &&
                oldCol != move.newCol &&
                getPiece(move.newCol, move.newRow) == null) {
            // Check if this is an en passant capture
            int enPassantTile = getTileNum(move.newCol, move.newRow);
            if (enPassantTile == this.enPassantTile) {
                // For en passant, the captured pawn is on the same row as the capturing pawn
                // but on the target column
                Piece capturedPawn = getPiece(move.newCol, oldRow);
                if (capturedPawn != null && capturedPawn.name.equals("Pawn")) {
                    // Add to captured pieces view before removing from board
                    addCapturedPiece(capturedPawn, move.piece.isWhite);
                    pieceList.remove(capturedPawn);
                }
            }
        }

        // Update en passant opportunities
        if (move.piece.name.equals("Pawn")) {
            // If a pawn moves two squares, set the en passant tile and flag
            if (Math.abs(move.newRow - oldRow) == 2) {
                int direction = move.piece.isWhite ? -1 : 1;
                enPassantTile = getTileNum(move.newCol, move.newRow + direction);
                if (move.piece instanceof Pawn) {
                    ((Pawn)move.piece).justMovedTwoSquares = true;
                }
            } else {
                enPassantTile = -1;
            }
        } else {
            // Reset en passant opportunity for any non-pawn move
            enPassantTile = -1;
        }

        // Handle castling
        if (move.piece.name.equals("King") && Math.abs(move.piece.col - move.newCol) > 1) {
            // Kingside castling
            if (move.newCol == 6) {
                Piece rook = getPiece(7, move.piece.row);
                if (rook != null) {
                    rook.col = 5;
                    rook.isFirstMove = false;
                    rook.updateVisualPosition(); // Update rook visual position
                }
            }
            // Queenside castling
            else if (move.newCol == 2) {
                Piece rook = getPiece(0, move.piece.row);
                if (rook != null) {
                    rook.col = 3;
                    rook.isFirstMove = false;
                    rook.updateVisualPosition(); // Update rook visual position
                }
            }
        }

        // Capture a piece if there is one at the destination
        Piece capturedPiece = getPiece(move.newCol, move.newRow);
        if (capturedPiece != null) {
            // Add captured piece to the view before removing from board
            addCapturedPiece(capturedPiece, move.piece.isWhite);
            pieceList.remove(capturedPiece);
        }

        // Move the piece
        move.piece.col = move.newCol;
        move.piece.row = move.newRow;
        move.piece.isFirstMove = false;
        move.piece.hasMoved = true;

        // Update the visual position of the moved piece
        move.piece.updateVisualPosition();

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
            // We have a pawn promotion situation
            if (view != null && view.getContext() != null) {
                // Show promotion dialog
                PawnPromotionDialog dialog = new PawnPromotionDialog(
                        view.getContext(),
                        pawn,
                        this,
                        pieceType -> promotePawn(pawn, pieceType)
                );
                dialog.show();
            } else {
                // Fallback to auto-promote to Queen if view isn't available (shouldn't happen)
                promotePawn(pawn, "Queen");
            }
        }
    }

    /**
     * Promote a pawn to another piece
     */
    private void promotePawn(Piece pawn, String newPieceName) {
        // Store the original position
        int col = pawn.col;
        int row = pawn.row;
        boolean isWhite = pawn.isWhite;

        // Remove the pawn from the board
        pieceList.remove(pawn);

        // Create a new piece of the desired type
        Piece newPiece = null;
        switch (newPieceName) {
            case "Queen":
                newPiece = new Queen(this, col, row, isWhite);
                break;
            case "Rook":
                newPiece = new Rook(this, col, row, isWhite);
                break;
            case "Bishop":
                newPiece = new Bishop(this, col, row, isWhite);
                break;
            case "Knight":
                newPiece = new Knight(this, col, row, isWhite);
                break;
        }

        // Add the new piece to the board
        if (newPiece != null) {
            pieceList.add(newPiece);

            // Mark the piece as having moved
            newPiece.isFirstMove = false;
            newPiece.hasMoved = true;

            // Load the sprite
            if (view != null && view.getContext() != null) {
                newPiece.loadSprite(view.getContext(), tileSize);
            }
        }

        // Update the board view
        if (view != null) {
            view.invalidate();
        }
    }

    /**
     * Reset the board and captured pieces views
     */
    public void reset() {
        // Reset captured pieces views
        if (capturedPiecesViewTop != null) {
            capturedPiecesViewTop.resetCapturedPieces();
        }
        if (capturedPiecesViewBottom != null) {
            capturedPiecesViewBottom.resetCapturedPieces();
        }
    }
}