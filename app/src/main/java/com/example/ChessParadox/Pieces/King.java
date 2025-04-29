// King.java
package com.example.ChessParadox.Pieces;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.ChessParadox.Main.CheckScanner;
import com.example.ChessParadox.Main.Chessboard;
import com.example.ChessParadox.Main.Move;
public class King extends Piece {
    public King(Chessboard chessBoard, int col, int row, boolean isWhite) {
        super(chessBoard, col, row, isWhite);
        this.name = "King";
    }

    @Override
    public void loadSprite(Context context, int tileSize) {
        @SuppressLint("DiscouragedApi") int resourceId = isWhite ?
                context.getResources().getIdentifier("king_white", "drawable", context.getPackageName()) :
                context.getResources().getIdentifier("king_black", "drawable", context.getPackageName());

        if (resourceId == 0) {
            resourceId = android.R.drawable.btn_default;
        }

        super.loadSprite(context, resourceId, tileSize);
    }
    @Override
    public boolean isValidMovement(int col, int row) {
        return Math.abs((col - this.col) * (row - this.row)) == 1 ||
                Math.abs(col - this.col) + Math.abs(row - this.row) == 1 ||
                canCastle(col, row);
    }

    private boolean canCastle(int col, int row) {
        if (this.row == row) {
            if (col == 6) {
                Piece rook = chessBoard.getPiece(7, row);
                if (rook != null && rook.isFirstMove && isFirstMove) {
                    return chessBoard.getPiece(5, row) == null &&
                            chessBoard.getPiece(6, row) == null &&
                            !new CheckScanner(chessBoard).isKingInCheck(new Move(chessBoard, this, 5, row));
                }
            } else if (col == 2) {
                Piece rook = chessBoard.getPiece(0, row);
                if (rook != null && rook.isFirstMove && isFirstMove) {
                    return chessBoard.getPiece(3, row) == null &&
                            chessBoard.getPiece(2, row) == null &&
                            chessBoard.getPiece(1, row) == null &&
                            !new CheckScanner(chessBoard).isKingInCheck(new Move(chessBoard, this, 3, row));
                }
            }
        }
        return false;
    }

}