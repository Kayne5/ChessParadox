package com.example.ChessParadox.TwoStepsAhead;

import com.example.ChessParadox.Classic.CapturedPiecesView;
import com.example.ChessParadox.Classic.CheckScanner;
import com.example.ChessParadox.Classic.Chessboard;
import com.example.ChessParadox.Classic.Chessboardview;
import com.example.ChessParadox.Classic.Move;
import com.example.ChessParadox.Pieces.King;
import com.example.ChessParadox.Pieces.Pawn;
import com.example.ChessParadox.Pieces.Piece;

/**
 * Represents the Two-Steps Ahead chess board that extends the classic chess board
 * In this variant, players can make two moves per turn
 */
public class TwoStepChessboard extends Chessboard {

    // Track turn state
    private boolean isFirstMove = true;
    private boolean isWhiteTurn = true;

    // Track the piece moved in the first move
    private Piece firstMovePiece = null;

    // Reference to the view for UI updates
    private TwoStepChessboardView twoStepView;

    public TwoStepChessboard(TwoStepChessboardView view) {
        super(view);
        this.twoStepView = view;
    }

    /**
     * Get the current turn state
     * @return true if it's the first move of the turn
     */
    public boolean isFirstMove() {
        return isFirstMove;
    }

    /**
     * Get the current player's turn
     * @return true if it's white's turn
     */
    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    /**
     * Get the piece that was moved in the first move of the current turn
     * @return the first moved piece or null if first move hasn't been made
     */
    public Piece getFirstMovePiece() {
        return firstMovePiece;
    }

    /**
     * Reset the first move piece
     */
    public void resetFirstMovePiece() {
        firstMovePiece = null;
    }

    /**
     * Determine if a move is valid according to Two-Steps Ahead chess rules
     */
    @Override
    public boolean isValidMove(Move move) {
        if (move == null || move.piece == null) {
            return false;
        }

        // Verify it's the correct player's turn
        if (move.piece.isWhite != isWhiteTurn) {
            return false;
        }

        // For the second move, you can't move the same piece twice
        if (!isFirstMove && move.piece == firstMovePiece) {
            return false;
        }

        // Perform base validation from the classic chess rules
        boolean baseValid = super.isValidMove(move);
        if (!baseValid) {
            return false;
        }

        return true;
    }

    /**
     * Make a move on the board
     */
    @Override
    public void makeMove(Move move) {
        // Make the move using the parent implementation
        super.makeMove(move);

        // If this was the first move of the turn
        if (isFirstMove) {
            // Record this piece as the first moved piece
            firstMovePiece = move.piece;

            // Switch to second move state
            isFirstMove = false;

            // Update the UI to show it's the second move
            if (twoStepView != null && twoStepView.getContext() != null) {
                twoStepView.updateTurnState(isWhiteTurn, isFirstMove);
            }
        } else {
            // After second move, switch to the other player
            isFirstMove = true;
            isWhiteTurn = !isWhiteTurn;
            firstMovePiece = null;

            // Update the UI to show it's the next player's turn
            if (twoStepView != null && twoStepView.getContext() != null) {
                twoStepView.updateTurnState(isWhiteTurn, isFirstMove);
            }
        }
    }

    /**
     * Check if a king is in checkmate
     */
    public boolean isKingInCheckmate(boolean isWhiteKing) {
        Piece king = findKing(isWhiteKing);
        if (king != null) {
            Move dummyMove = new Move(this, king, king.col, king.row);

            // Check if king is in check
            CheckScanner checkScanner = getCheckScanner();
            if (!checkScanner.isKingInCheck(dummyMove)) {
                return false;
            }

            // Check if any move can get the king out of check
            for (Piece piece : pieceList) {
                if (piece.isWhite == isWhiteKing) {
                    for (int col = 0; col < 8; col++) {
                        for (int row = 0; row < 8; row++) {
                            Move move = new Move(this, piece, col, row);
                            if (super.isValidMove(move)) {
                                return false;
                            }
                        }
                    }
                }
            }

            return true;
        }
        return false;
    }

    /**
     * Reset the board and game state
     */
    @Override
    public void reset() {
        super.reset();

        // Reset turn state
        isFirstMove = true;
        isWhiteTurn = true;
        firstMovePiece = null;

        // Update the UI
        if (twoStepView != null && twoStepView.getContext() != null) {
            twoStepView.updateTurnState(isWhiteTurn, isFirstMove);
        }
    }
}