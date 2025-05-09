package com.example.ChessParadox.Pieces;

import com.example.ChessParadox.Classic.Chessboard;

public class Bishop extends Piece {
    public Bishop(Chessboard chessBoard, int col, int row, boolean isWhite) {
        super(chessBoard, col, row, isWhite);
        this.name = "Bishop";
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        return Math.abs(this.col - col) == Math.abs(this.row - row);
    }

    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        // Calculate direction
        int colDir = Integer.compare(col - this.col, 0);
        int rowDir = Integer.compare(row - this.row, 0);

        // Check for pieces in path
        for (int i = 1; i < Math.abs(this.col - col); i++) {
            int checkCol = this.col + (i * colDir);
            int checkRow = this.row + (i * rowDir);

            if (chessBoard.getPiece(checkCol, checkRow) != null) {
                return true; // Path is blocked
            }
        }

        return false;
    }
}