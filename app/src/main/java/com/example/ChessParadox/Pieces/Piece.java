package com.example.ChessParadox.Pieces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import com.example.ChessParadox.Classic.Chessboard;

public abstract class Piece {
    private static final String TAG = "Piece";
    public int col, row;
    public int xPos, yPos;

    public boolean isWhite;
    public String name;
    public boolean isFirstMove = true;
    public boolean hasMoved;

    public Bitmap sprite;
    protected Chessboard chessBoard;

    public Piece(Chessboard chessBoard, int col, int row, boolean isWhite) {
        this.chessBoard = chessBoard;
        this.col = col;
        this.row = row;
        this.isWhite = isWhite;

        // Initialize positions (will be updated properly in onSizeChanged)
        this.xPos = col * (chessBoard != null && chessBoard.tileSize > 0 ? chessBoard.tileSize : 100);
        this.yPos = row * (chessBoard != null && chessBoard.tileSize > 0 ? chessBoard.tileSize : 100);
    }

    public void draw(Canvas canvas) {
        if (canvas != null && sprite != null) {
            canvas.drawBitmap(sprite, xPos, yPos, null);
        }
    }

    /**
     * Load sprite with correct naming pattern matching the resource files
     */
    public void loadSprite(Context context, int tileSize) {
        try {
            if (context == null) {
                Log.e(TAG, "Context is null when loading sprite");
                return;
            }

            // Match resource naming: white_pawn.png, black_knight.png, etc.
            String colorPrefix = isWhite ? "white" : "black";
            String resourceName = colorPrefix + "_" + name.toLowerCase();

            int resourceId = context.getResources().getIdentifier(
                    resourceName, "drawable", context.getPackageName());

            if (resourceId == 0) {
                Log.e(TAG, "Resource not found for: " + resourceName);
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

    /**
     * Update the piece's visual position based on logical position
     */
    public void updateVisualPosition() {
        if (chessBoard != null) {
            this.xPos = this.col * chessBoard.tileSize;
            this.yPos = this.row * chessBoard.tileSize;
        }
    }

    /**
     * Check if the piece can move to the target position according to its movement pattern
     */
    public abstract boolean isValidMovement(int col, int row);

    /**
     * Check if the piece's path to the target position is obstructed by other pieces
     * Default implementation assumes no path checking needed (like Knight)
     */
    public boolean moveCollidesWithPiece(int col, int row) {
        return false;
    }
}