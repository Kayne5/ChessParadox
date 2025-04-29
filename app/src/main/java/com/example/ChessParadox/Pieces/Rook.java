package com.example.ChessParadox.Pieces;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.ChessParadox.Main.Chessboard;

public class Rook extends Piece {
    public Rook(Chessboard chessBoard, int col, int row, boolean isWhite) {
        super(chessBoard, col, row, isWhite);
        this.name = "Rook";
    }

    @Override
    public void loadSprite(Context context, int tileSize) {
        @SuppressLint("DiscouragedApi") int resourceId = isWhite ?
                context.getResources().getIdentifier("rook_white", "drawable", context.getPackageName()) :
                context.getResources().getIdentifier("rook_black", "drawable", context.getPackageName());

        if (resourceId == 0) {
            resourceId = android.R.drawable.btn_default;
        }

        super.loadSprite(context, resourceId, tileSize);
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        return this.col == col || this.row == row;
    }

    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        // left
        if (this.col > col)
            for (int c = this.col - 1; c > col; c--)
                if (chessBoard.getPiece(c, this.row) != null)
                    return true;

        // right
        if (this.col < col)
            for (int c = this.col + 1; c < col; c++)
                if (chessBoard.getPiece(c, this.row) != null)
                    return true;

        // up
        if (this.row > row)
            for (int r = this.row - 1; r > row; r--)
                if (chessBoard.getPiece(this.col, r) != null)
                    return true;

        // down
        if (this.row < row)
            for (int r = this.row + 1; r < row; r++)
                if (chessBoard.getPiece(this.col, r) != null)
                    return true;

        return false;
    }
}