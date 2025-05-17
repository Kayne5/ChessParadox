package com.example.ChessParadox.Classic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.example.ChessParadox.Pieces.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom view to display captured pieces on the side of the chess board
 * Groups identical pieces and shows a count indicator for multiples
 */
public class CapturedPiecesView extends View {
    private static final int PIECE_SPACING = 8; // Spacing between pieces in dp
    private static final float SCALE_FACTOR = 0.8f; // Scale factor for captured pieces
    private static final float COUNT_TEXT_SIZE_FACTOR = 0.7f; // Size factor for the count text

    private List<Piece> capturedByWhite = new ArrayList<>();
    private List<Piece> capturedByBlack = new ArrayList<>();
    private int pieceSize;
    private Paint backgroundPaint;
    private Paint countTextPaint;
    private boolean isHorizontal = true; // Default to horizontal layout like in the image

    // Piece values for sorting (standard chess piece values)
    private static final Map<String, Integer> PIECE_VALUES = new HashMap<>();
    static {
        PIECE_VALUES.put("Queen", 9);
        PIECE_VALUES.put("Rook", 5);
        PIECE_VALUES.put("Bishop", 3);
        PIECE_VALUES.put("Knight", 3);
        PIECE_VALUES.put("Pawn", 1);
        PIECE_VALUES.put("King", 0); // Kings shouldn't be captured, but included for completeness
    }

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
        // Create dark transparent background
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#55000000")); // Semi-transparent black

        // Create text paint for piece counts
        countTextPaint = new Paint();
        countTextPaint.setColor(Color.WHITE);
        countTextPaint.setTextAlign(Paint.Align.RIGHT); // Right-aligned for bottom-right position
        countTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        countTextPaint.setAntiAlias(true);

        // Add shadow to make count more visible against any background
        countTextPaint.setShadowLayer(2f, 1f, 1f, Color.BLACK);
    }

    /**
     * Set the orientation of the view
     * @param horizontal true for horizontal, false for vertical
     */
    public void setOrientation(boolean horizontal) {
        this.isHorizontal = horizontal;
        invalidate();
    }

    /**
     * Set the size of pieces based on the chessboard tile size
     * @param tileSize The size of a chess board tile
     */
    public void setPieceSize(int tileSize) {
        this.pieceSize = (int)(tileSize * SCALE_FACTOR);
        countTextPaint.setTextSize(pieceSize * COUNT_TEXT_SIZE_FACTOR);
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
        capturedByWhite.sort((p1, p2) -> getPieceValue(p2) - getPieceValue(p1));
        capturedByBlack.sort((p1, p2) -> getPieceValue(p2) - getPieceValue(p1));
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

        if (isHorizontal) {
            drawHorizontal(canvas);
        } else {
            drawVertical(canvas);
        }
    }

    /**
     * Draw pieces in vertical orientation (white at top, black at bottom)
     */
    private void drawVertical(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        // Draw pieces captured by white (black pieces) in the top half
        drawGroupedPieces(canvas, capturedByBlack, 0, 0, width, height / 2);

        // Draw pieces captured by black (white pieces) in the bottom half
        drawGroupedPieces(canvas, capturedByWhite, 0, height / 2, width, height);
    }

    /**
     * Draw pieces in horizontal orientation (white on left, black on right)
     */
    private void drawHorizontal(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        // Draw pieces captured by white (black pieces) on the left half
        drawGroupedPieces(canvas, capturedByBlack, 0, 0, width / 2, height);

        // Draw pieces captured by black (white pieces) on the right half
        drawGroupedPieces(canvas, capturedByWhite, width / 2, 0, width, height);
    }

    /**
     * Group pieces by type and color, then draw them with counts
     * @param canvas The canvas to draw on
     * @param pieces List of pieces to draw
     * @param left Left boundary of drawing area
     * @param top Top boundary of drawing area
     * @param right Right boundary of drawing area
     * @param bottom Bottom boundary of drawing area
     */
    private void drawGroupedPieces(Canvas canvas, List<Piece> pieces, int left, int top, int right, int bottom) {
        if (pieces.isEmpty()) return;

        // Group identical pieces and count them
        Map<String, List<Piece>> groupedPieces = groupPiecesByTypeAndColor(pieces);

        // Calculate how many pieces can fit in a row
        int width = right - left;
        int height = bottom - top;
        int maxPiecesPerRow = Math.max(1, width / (pieceSize + PIECE_SPACING));

        int x = left + PIECE_SPACING;
        int y = top + (height - pieceSize) / 2; // Center vertically
        int pieceCount = 0;

        for (Map.Entry<String, List<Piece>> entry : groupedPieces.entrySet()) {
            List<Piece> pieceGroup = entry.getValue();
            if (pieceGroup.isEmpty()) continue;

            Piece piece = pieceGroup.get(0);  // Get the first piece as a representative

            // Draw the actual piece image
            Rect destRect = new Rect(x, y, x + pieceSize, y + pieceSize);
            if (piece.sprite != null) {
                canvas.drawBitmap(piece.sprite, null, destRect, null);
            }

            // Draw count indicator if more than one piece of this type
            if (pieceGroup.size() > 1) {
                String countText = String.valueOf(pieceGroup.size());

                // Position text at the bottom right corner of the piece
                float textX = x + pieceSize - 5; // Small padding from right edge
                float textY = y + pieceSize - 5; // Small padding from bottom edge

                // Draw the count indicator
                canvas.drawText(countText, textX, textY, countTextPaint);
            }

            // Update position for next piece
            x += pieceSize + PIECE_SPACING;
            pieceCount++;

            // Move to next row if needed
            if (pieceCount > 0 && pieceCount % maxPiecesPerRow == 0) {
                x = left + PIECE_SPACING;
                y += pieceSize + PIECE_SPACING;

                // Check if we're out of vertical space
                if (y + pieceSize > bottom) {
                    break; // Stop drawing if we're out of room
                }
            }
        }
    }

    /**
     * Group pieces by type and color
     */
    private Map<String, List<Piece>> groupPiecesByTypeAndColor(List<Piece> pieces) {
        Map<String, List<Piece>> groupedPieces = new LinkedHashMap<>();

        for (Piece piece : pieces) {
            String key = piece.isWhite + "_" + piece.name;

            if (!groupedPieces.containsKey(key)) {
                groupedPieces.put(key, new ArrayList<>());
            }

            groupedPieces.get(key).add(piece);
        }

        return groupedPieces;
    }

    /**
     * Get the standard chess value of a piece
     */
    private int getPieceValue(Piece piece) {
        Integer value = PIECE_VALUES.get(piece.name);
        return value != null ? value : 0;
    }
}
