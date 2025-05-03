package com.example.ChessParadox.Main;

import com.example.ChessParadox.Pieces.Piece;

/**
 * Represents a chess move from one position to another
 */
public class Move {
    public final int oldCol;
    public final int oldRow;
    public final int newCol;
    public final int newRow;
    public final Piece piece;

    /**
     * Creates a new move object
     *
     * @param chessboard The chessboard the move is made on
     * @param piece The piece being moved
     * @param newCol The destination column
     * @param newRow The destination row
     */
    public Move(Chessboard chessboard, Piece piece, int newCol, int newRow) {
        this.piece = piece;
        this.oldCol = piece.col;
        this.oldRow = piece.row;
        this.newCol = newCol;
        this.newRow = newRow;
    }
}