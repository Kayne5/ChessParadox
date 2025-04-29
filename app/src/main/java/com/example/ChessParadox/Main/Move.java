package com.example.ChessParadox.Main;

import com.example.ChessParadox.Pieces.Piece;

public class Move {

    public int oldCol;
    public int oldRow;
    public int newRow;
    public int newCol;

    public Piece piece;
    public Piece capture;

    public Move(Chessboard chessboard, Piece piece, int newCol, int newRow) {
        this.oldCol = piece.col;
        this.oldRow = piece.row;
        this.newCol = newCol;
        this.newRow = newRow;

        this.piece = piece;
        this.capture = null;
    }
}