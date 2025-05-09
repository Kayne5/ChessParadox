package com.example.ChessParadox.Pieces;

import com.example.ChessParadox.Main.Chessboard;

public class Queen extends Piece {
    private final Rook rookMovement;
    private final Bishop bishopMovement;

    public Queen(Chessboard chessBoard, int col, int row, boolean isWhite) {
        super(chessBoard, col, row, isWhite);
        this.name = "Queen";

        // Create helper pieces to reuse movement logic
        this.rookMovement = new Rook(chessBoard, col, row, isWhite);
        this.bishopMovement = new Bishop(chessBoard, col, row, isWhite);
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        // Queen moves like a rook or bishop
        return this.col == col || this.row == row || Math.abs(this.col - col) == Math.abs(this.row - row);
    }

    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        // Update position of helper pieces to match queen's current position
        rookMovement.col = this.col;
        rookMovement.row = this.row;
        bishopMovement.col = this.col;
        bishopMovement.row = this.row;

        // Check using rook movement for horizontal/vertical
        if (this.col == col || this.row == row) {
            return rookMovement.moveCollidesWithPiece(col, row);
        }
        // Check using bishop movement for diagonal
        else {
            return bishopMovement.moveCollidesWithPiece(col, row);
        }
    }
}