package com.example.ChessParadox.Pieces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import com.example.ChessParadox.Main.Chessboard;

public abstract class Piece {
    private static final String TAG = "Piece";
    public int col, row;
    public int xPos, yPos;

    public boolean isWhite;
    public String name;
    public boolean isFirstMove = true;

    protected Bitmap sprite;
    protected Chessboard chessBoard;

    public Piece(Chessboard chessBoard, int col, int row, boolean isWhite) {
        this.chessBoard = chessBoard;
        this.col = col;
        this.row = row;
        this.xPos = col * (chessBoard != null && chessBoard.tileSize > 0 ? chessBoard.tileSize : 100);
        this.yPos = row * (chessBoard != null && chessBoard.tileSize > 0 ? chessBoard.tileSize : 100);
        this.isWhite = isWhite;
    }

    public void draw(Canvas canvas, int tileSize) {
        try {
            if (canvas != null && sprite != null) {
                canvas.drawBitmap(sprite, xPos, yPos, null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error drawing piece: " + name, e);
        }
    }

    public void loadSprite(Context context, int resourceId, int tileSize) {
        try {
            if (context == null) {
                Log.e(TAG, "Context is null when loading sprite");
                return;
            }

            if (resourceId == 0) {
                Log.e(TAG, "Invalid resource ID for piece: " + name);
                return;
            }

            Bitmap original = BitmapFactory.decodeResource(context.getResources(), resourceId);
            if (original == null) {
                Log.e(TAG, "Could not decode resource for piece: " + name);
                return;
            }

            this.sprite = Bitmap.createScaledBitmap(original, tileSize, tileSize, true);
            original.recycle();
        } catch (Exception e) {
            Log.e(TAG, "Error loading sprite for piece: " + name, e);
        }
    }

    public abstract boolean isValidMovement(int col, int row);

    public boolean moveCollidesWithPiece(int col, int row) {
        return false;
    }

    public void loadSprite(Context context, int tileSize) {
        // This is a stub that should be overridden by subclasses
        Log.w(TAG, "loadSprite not implemented for piece: " + name);
    }
}