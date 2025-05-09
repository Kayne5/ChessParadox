package com.example.ChessParadox.Pieces;

import com.example.ChessParadox.Classic.Chessboard;

public class Pawn extends Piece {
    public Pawn(Chessboard chessBoard, int col, int row, boolean isWhite) {
        super(chessBoard, col, row, isWhite);
        this.name = "Pawn";
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        // Direction depends on color (white moves up, black moves down)
        int colorDir = isWhite ? -1 : 1;

        // Forward one square
        if (this.col == col && row == this.row + colorDir) {
            return chessBoard.getPiece(col, row) == null;
        }

        // Forward two squares on first move
        if (isFirstMove && this.col == col && row == this.row + (2 * colorDir)) {
            // Check both squares are empty
            return chessBoard.getPiece(col, this.row + colorDir) == null &&
                    chessBoard.getPiece(col, row) == null;
        }

        // Diagonal captures
        if ((col == this.col - 1 || col == this.col + 1) && row == this.row + colorDir) {
            // Regular capture
            Piece targetPiece = chessBoard.getPiece(col, row);
            if (targetPiece != null) {
                return !chessBoard.sameTeam(this, targetPiece);
            }

            // En passant capture
            return chessBoard.getTileNum(col, row) == chessBoard.enPassantTile;
        }

        return false;
    }

    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        // Collision detection for pawns is handled in isValidMovement
        return false;
    }
}