// Knight.java
package com.example.ChessParadox.Pieces;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.ChessParadox.Main.Chessboard;

public class Knight extends Piece {
    public Knight(Chessboard chessBoard, int col, int row, boolean isWhite) {
        super(chessBoard, col, row, isWhite);
        this.name = "Knight";
    }

    @Override
    public void loadSprite(Context context, int tileSize) {
        @SuppressLint("DiscouragedApi") int resourceId = isWhite ?
                context.getResources().getIdentifier("knight_white", "drawable", context.getPackageName()) :
                context.getResources().getIdentifier("knight_black", "drawable", context.getPackageName());

        if (resourceId == 0) {
            resourceId = android.R.drawable.btn_default;
        }

        super.loadSprite(context, resourceId, tileSize);
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        return Math.abs(col - this.col) * Math.abs(row - this.row) == 2;
    }
}