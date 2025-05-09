package com.example.ChessParadox.Pieces;

import com.example.ChessParadox.Classic.Chessboard;

public class Rook extends Piece {
    public Rook(Chessboard chessBoard, int col, int row, boolean isWhite) {
        super(chessBoard, col, row, isWhite);
        this.name = "Rook";
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        return this.col == col || this.row == row;
    }

    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        // Moving horizontally
        if (this.row == row) {
            int start = Math.min(this.col, col) + 1;
            int end = Math.max(this.col, col);

            for (int c = start; c < end; c++) {
                if (chessBoard.getPiece(c, row) != null) {
                    return true; // Path is blocked
                }
            }
        }
        // Moving vertically
        else if (this.col == col) {
            int start = Math.min(this.row, row) + 1;
            int end = Math.max(this.row, row);

            for (int r = start; r < end; r++) {
                if (chessBoard.getPiece(col, r) != null) {
                    return true; // Path is blocked
                }
            }
        }

        return false;
    }
}