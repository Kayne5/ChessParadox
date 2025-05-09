package com.example.ChessParadox.Pieces;

import com.example.ChessParadox.Classic.Chessboard;

public class King extends Piece {
    public King(Chessboard chessBoard, int col, int row, boolean isWhite) {
        super(chessBoard, col, row, isWhite);
        this.name = "King";
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        // Normal one-square movement in any direction
        boolean normalMove = Math.abs(col - this.col) <= 1 && Math.abs(row - this.row) <= 1 &&
                (col != this.col || row != this.row);

        // Check for castling (king moves 2 squares horizontally)
        boolean castlingMove = this.row == row && Math.abs(col - this.col) == 2;

        return normalMove || castlingMove;
    }
}