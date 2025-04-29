// Queen.java
package com.example.ChessParadox.Pieces;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.ChessParadox.Main.Chessboard;

public class Queen extends Piece {
    public Queen(Chessboard chessBoard, int col, int row, boolean isWhite) {
        super(chessBoard, col, row, isWhite);
        this.name = "Queen";
    }

    @Override
    public void loadSprite(Context context, int tileSize) {
        @SuppressLint("DiscouragedApi") int resourceId = isWhite ?
                context.getResources().getIdentifier("queen_white", "drawable", context.getPackageName()) :
                context.getResources().getIdentifier("queen_black", "drawable", context.getPackageName());

        if (resourceId == 0) {
            resourceId = android.R.drawable.btn_default;
        }

        super.loadSprite(context, resourceId, tileSize);
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        return this.col == col || this.row == row || Math.abs(this.col - col) == Math.abs(this.row - row);
    }

    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        if (this.col == col || this.row == row) {
            // Rook-like movement
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

        } else {
            // Bishop-like movement
            // up left
            if (this.col > col && this.row > row)
                for (int i = 1; i < Math.abs(this.col - col); i++)
                    if (chessBoard.getPiece(this.col - i, this.row - i) != null)
                        return true;

            // up right
            if (this.col < col && this.row > row)
                for (int i = 1; i < Math.abs(this.col - col); i++)
                    if (chessBoard.getPiece(this.col + i, this.row - i) != null)
                        return true;

            // down left
            if (this.col > col && this.row < row)
                for (int i = 1; i < Math.abs(this.col - col); i++)
                    if (chessBoard.getPiece(this.col - i, this.row + i) != null)
                        return true;

            // down right
            if (this.col < col && this.row < row)
                for (int i = 1; i < Math.abs(this.col - col); i++)
                    if (chessBoard.getPiece(this.col + i, this.row + i) != null)
                        return true;
        }
        return false;
    }
}