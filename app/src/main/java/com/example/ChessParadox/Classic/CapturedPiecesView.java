package com.example.ChessParadox.Classic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.ChessParadox.Pieces.Piece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Custom view to display captured pieces on the side of the chess board
 */
public class CapturedPiecesView extends View {
    private static final int PIECE_SPACING = 5; // Spacing between pieces in dp
    private static final float SCALE_FACTOR = 0.7f; // Scale factor for captured pieces

    private List<Piece> capturedByWhite = new ArrayList<>();
    private List<Piece> capturedByBlack = new ArrayList<>();
    private int pieceSize;
    private Paint backgroundPaint;
    private boolean isVertical = true; // Whether the view is oriented vertically

    // Piece values for sorting (standard chess piece values)
    private static final int PAWN_VALUE = 1;
    private static final int KNIGHT_VALUE = 3;
    private static final int BISHOP_VALUE = 3;
    private static final int ROOK_VALUE = 5;
    private static final int QUEEN_VALUE = 9;
    private static final int KING_VALUE = 0; // Kings shouldn't be captured, but included for completeness

    public CapturedPiecesView(Context context) {
        super(context);
        init();
    }

    public CapturedPiecesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CapturedPiecesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#DDDDDD")); // Light gray background
    }

    /**
     * Set the orientation of the view
     * @param vertical true for vertical, false for horizontal
     */
    public void setOrientation(boolean vertical) {
        this.isVertical = vertical;
        invalidate();
    }

    /**
     * Set the size of pieces based on the chessboard tile size
     * @param tileSize The size of a chess board tile
     */
    public void setPieceSize(int tileSize) {
        this.pieceSize = (int)(tileSize * SCALE_FACTOR);
        invalidate();
    }

    /**
     * Add a captured piece to the appropriate list
     * @param piece The captured piece
     * @param capturedByWhite Whether it was captured by white (true) or black (false)
     */
    public void addCapturedPiece(Piece piece, boolean capturedByWhite) {
        if (capturedByWhite) {
            this.capturedByWhite.add(piece);
        } else {
            this.capturedByBlack.add(piece);
        }

        // Sort pieces by value (highest value first)
        sortPieces();

        // Force redraw
        invalidate();
    }

    /**
     * Sort the captured pieces lists by piece value
     */
    private void sortPieces() {
        Comparator<Piece> valueComparator = (p1, p2) -> {
            return getPieceValue(p2) - getPieceValue(p1);
        };

        Collections.sort(capturedByWhite, valueComparator);
        Collections.sort(capturedByBlack, valueComparator);
    }

    /**
     * Get the standard chess value of a piece
     */
    private int getPieceValue(Piece piece) {
        switch (piece.name) {
            case "Queen": return QUEEN_VALUE;
            case "Rook": return ROOK_VALUE;
            case "Bishop": return BISHOP_VALUE;
            case "Knight": return KNIGHT_VALUE;
            case "Pawn": return PAWN_VALUE;
            case "King": return KING_VALUE;
            default: return 0;
        }
    }

    /**
     * Clear all captured pieces
     */
    public void resetCapturedPieces() {
        capturedByWhite.clear();
        capturedByBlack.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw background
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        if (isVertical) {
            drawVertical(canvas);
        } else {
            drawHorizontal(canvas);
        }
    }

    /**
     * Draw pieces in vertical orientation (white at top, black at bottom)
     */
    private void drawVertical(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int halfHeight = height / 2;

        // Calculate how many pieces can fit in a row
        int piecesPerRow = Math.max(1, width / (pieceSize + PIECE_SPACING));

        // Draw pieces captured by white (black pieces) in the top half
        drawPiecesGrid(canvas, capturedByBlack, 0, 0, width, halfHeight, piecesPerRow);

        // Draw a divider line
        Paint dividerPaint = new Paint();
        dividerPaint.setColor(Color.GRAY);
        canvas.drawLine(0, halfHeight, width, halfHeight, dividerPaint);

        // Draw pieces captured by black (white pieces) in the bottom half
        drawPiecesGrid(canvas, capturedByWhite, 0, halfHeight, width, height, piecesPerRow);
    }

    /**
     * Draw pieces in horizontal orientation (white on left, black on right)
     */
    private void drawHorizontal(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int halfWidth = width / 2;

        // Calculate how many pieces can fit in a row
        int piecesPerCol = Math.max(1, height / (pieceSize + PIECE_SPACING));

        // Draw pieces captured by white (black pieces) on the left half
        drawPiecesGrid(canvas, capturedByBlack, 0, 0, halfWidth, height, piecesPerCol);

        // Draw a divider line
        Paint dividerPaint = new Paint();
        dividerPaint.setColor(Color.GRAY);
        canvas.drawLine(halfWidth, 0, halfWidth, height, dividerPaint);

        // Draw pieces captured by black (white pieces) on the right half
        drawPiecesGrid(canvas, capturedByWhite, halfWidth, 0, width, height, piecesPerCol);
    }

    /**
     * Draw pieces in a grid layout
     */
    private void drawPiecesGrid(Canvas canvas, List<Piece> pieces, int left, int top, int right, int bottom, int itemsPerRow) {
        int x = left + PIECE_SPACING;
        int y = top + PIECE_SPACING;
        int count = 0;

        for (Piece piece : pieces) {
            // Load sprite if not already loaded
            if (piece.sprite == null && getContext() != null) {
                piece.loadSprite(getContext(), pieceSize);
            }

            // Calculate position
            if (isVertical) {
                // For vertical layout, we fill rows first
                if (count > 0 && count % itemsPerRow == 0) {
                    x = left + PIECE_SPACING;
                    y += pieceSize + PIECE_SPACING;
                }
            } else {
                // For horizontal layout, we fill columns first
                if (count > 0 && count % itemsPerRow == 0) {
                    y = top + PIECE_SPACING;
                    x += pieceSize + PIECE_SPACING;
                }
            }

            // Draw the piece
            if (piece.sprite != null) {
                canvas.drawBitmap(piece.sprite, x, y, null);
            }

            // Update position for next piece
            if (isVertical) {
                x += pieceSize + PIECE_SPACING;
            } else {
                y += pieceSize + PIECE_SPACING;
            }

            count++;
        }
    }
}