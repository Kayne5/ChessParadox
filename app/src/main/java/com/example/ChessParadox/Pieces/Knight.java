package com.example.ChessParadox.Pieces;

import com.example.ChessParadox.Main.Chessboard;

public class Knight extends Piece {
    public Knight(Chessboard chessBoard, int col, int row, boolean isWhite) {
        super(chessBoard, col, row, isWhite);
        this.name = "Knight";
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        return Math.abs(col - this.col) * Math.abs(row - this.row) == 2;
    }

    // Knights jump over pieces, so no collision check needed
}