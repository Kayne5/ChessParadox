// Bishop.java
package com.example.ChessParadox.Pieces;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.ChessParadox.Main.Chessboard;

public class Bishop extends Piece {
    public Bishop(Chessboard chessBoard, int col, int row, boolean isWhite) {
        super(chessBoard, col, row, isWhite);
        this.name = "Bishop";
    }

    @Override
    public void loadSprite(Context context, int tileSize) {
        @SuppressLint("DiscouragedApi") int resourceId = isWhite ?
                context.getResources().getIdentifier("bishop_white", "drawable", context.getPackageName()) :
                context.getResources().getIdentifier("bishop_black", "drawable", context.getPackageName());

        // Fallback to a default resource if the specific piece image is not found
        if (resourceId == 0) {
            // Use a placeholder or default resource
            resourceId = android.R.drawable.btn_default;  // Just a placeholder
        }

        super.loadSprite(context, resourceId, tileSize);
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        return Math.abs(this.col - col) == Math.abs(this.row - row);
    }

    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
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

        return false;
    }
}