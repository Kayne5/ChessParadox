// Pawn.java
package com.example.ChessParadox.Pieces;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.ChessParadox.Main.Chessboard;

public class Pawn extends Piece {
    public Pawn(Chessboard chessBoard, int col, int row, boolean isWhite) {
        super(chessBoard, col, row, isWhite);
        this.name = "Pawn";
    }

    @Override
    public void loadSprite(Context context, int tileSize) {
        @SuppressLint("DiscouragedApi") int resourceId = isWhite ?
                context.getResources().getIdentifier("pawn_white", "drawable", context.getPackageName()) :
                context.getResources().getIdentifier("pawn_black", "drawable", context.getPackageName());

        if (resourceId == 0) {
            resourceId = android.R.drawable.btn_default;
        }

        super.loadSprite(context, resourceId, tileSize);
    }

    @Override
    public boolean isValidMovement(int col, int row) {
        int colorIndex = isWhite ? 1 : -1;

        // push pawn 1
        if (this.col == col && row == this.row - colorIndex && chessBoard.getPiece(col, row) == null)
            return true;

        // push pawn 2
        if (isFirstMove && this.col == col && row == this.row - colorIndex * 2 &&
                chessBoard.getPiece(col, row) == null && chessBoard.getPiece(col, row + colorIndex) == null)
            return true;

        // capture left
        if (col == this.col - 1 && row == this.row - colorIndex && chessBoard.getPiece(col, row) != null)
            return true;

        // capture right
        if (col == this.col + 1 && row == this.row - colorIndex && chessBoard.getPiece(col, row) != null)
            return true;

        // en passant left
        if (chessBoard.getTileNum(col, row) == chessBoard.enPassantTile &&
                col == this.col - 1 && row == this.row - colorIndex &&
                chessBoard.getPiece(col, row + colorIndex) != null) {
            return true;
        }

        // en passant right
        if (chessBoard.getTileNum(col, row) == chessBoard.enPassantTile &&
                col == this.col + 1 && row == this.row - colorIndex &&
                chessBoard.getPiece(col, row + colorIndex) != null) {
            return true;
        }

        return false;
    }
}