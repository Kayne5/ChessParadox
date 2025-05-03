package com.example.ChessParadox.Main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.ChessParadox.Pieces.*;

import java.util.ArrayList;

/**
 * Custom view that renders the chess board and handles user interactions
 */
public class Chessboardview extends View {
    private static final String TAG = "ChessboardView";

    // Board properties
    public int tileSize;
    public ArrayList<Piece> pieceList = new ArrayList<>();

    // Game state
    private boolean isWhiteToMove = true;
    private boolean isGameOver = false;
    private String gameResult = "";

    // UI properties
    private Paint lightTilePaint;
    private Paint darkTilePaint;
    private Paint highlightPaint;
    private Paint textPaint;

    // Drag and drop
    private float touchX, touchY;
    private boolean draggingPiece = false;

    // Game logic
    private Chessboard chessBoard;
    public Piece selectedPiece;

    public Chessboardview(Context context) {
        super(context);
        init();
    }

    public Chessboardview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Chessboardview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Initialize paints
        lightTilePaint = new Paint();
        lightTilePaint.setColor(Color.parseColor("#f0d9b5"));

        darkTilePaint = new Paint();
        darkTilePaint.setColor(Color.parseColor("#b58863"));

        highlightPaint = new Paint();
        highlightPaint.setColor(Color.parseColor("#a2e375"));
        highlightPaint.setAlpha(180);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Set a default tile size that will be updated in onSizeChanged
        tileSize = 100;

        // Initialize the chess board
        chessBoard = new Chessboard(this);

        // Set up the board
        setupBoard();

        // Initialize the check scanner after the board is set up
        chessBoard.initCheckScanner();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Calculate the tile size based on the smallest dimension
        tileSize = Math.min(w, h) / 8;

        // Update the chessboard tile size
        chessBoard.tileSize = tileSize;

        // Update all pieces with new positions and load sprites
        for (Piece piece : pieceList) {
            piece.xPos = piece.col * tileSize;
            piece.yPos = piece.row * tileSize;
            piece.loadSprite(getContext(), tileSize);
        }

        // Force redraw with new sizes
        invalidate();
    }

    /**
     * Set up the initial chess board with all pieces
     */
    public void setupBoard() {
        pieceList.clear();

        // Black pieces (top of the board)
        addPiece(new Rook(chessBoard, 0, 0, false));
        addPiece(new Knight(chessBoard, 1, 0, false));
        addPiece(new Bishop(chessBoard, 2, 0, false));
        addPiece(new Queen(chessBoard, 3, 0, false));
        addPiece(new King(chessBoard, 4, 0, false));
        addPiece(new Bishop(chessBoard, 5, 0, false));
        addPiece(new Knight(chessBoard, 6, 0, false));
        addPiece(new Rook(chessBoard, 7, 0, false));

        for (int col = 0; col < 8; col++) {
            addPiece(new Pawn(chessBoard, col, 1, false));
        }

        // White pieces (bottom of the board)
        addPiece(new Rook(chessBoard, 0, 7, true));
        addPiece(new Knight(chessBoard, 1, 7, true));
        addPiece(new Bishop(chessBoard, 2, 7, true));
        addPiece(new Queen(chessBoard, 3, 7, true));
        addPiece(new King(chessBoard, 4, 7, true));
        addPiece(new Bishop(chessBoard, 5, 7, true));
        addPiece(new Knight(chessBoard, 6, 7, true));
        addPiece(new Rook(chessBoard, 7, 7, true));

        for (int col = 0; col < 8; col++) {
            addPiece(new Pawn(chessBoard, col, 6, true));
        }

        // Update the chess board's piece list
        chessBoard.pieceList = pieceList;

        // Initialize piece positions and load sprites
        for (Piece piece : pieceList) {
            piece.xPos = piece.col * tileSize;
            piece.yPos = piece.row * tileSize;
            piece.loadSprite(getContext(), tileSize);
        }

        // Reset game state
        isWhiteToMove = true;
        isGameOver = false;
        gameResult = "";
        selectedPiece = null;
    }

    private void addPiece(Piece piece) {
        pieceList.add(piece);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the chess board
        drawBoard(canvas);

        // Draw highlights for valid moves if a piece is selected
        if (selectedPiece != null) {
            highlightValidMoves(canvas);
        }

        // Draw all chess pieces except the selected one being dragged
        for (Piece piece : pieceList) {
            if (piece != selectedPiece || !draggingPiece) {
                piece.draw(canvas);
            }
        }

        // Draw the selected piece on top if being dragged
        if (selectedPiece != null && draggingPiece) {
            selectedPiece.draw(canvas);
        }

        // Display game over message if the game has ended
        if (isGameOver) {
            drawGameOverMessage(canvas);
        }

        // Continue animation if dragging
        if (draggingPiece) {
            invalidate();
        }
    }

    /**
     * Draw the chess board with alternating light and dark tiles
     */
    private void drawBoard(Canvas canvas) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Determine if this is a light or dark tile
                boolean isLightTile = (row + col) % 2 == 0;

                // Calculate the position and size of this tile
                float left = col * tileSize;
                float top = row * tileSize;
                float right = left + tileSize;
                float bottom = top + tileSize;

                // Draw the tile
                canvas.drawRect(left, top, right, bottom, isLightTile ? lightTilePaint : darkTilePaint);
            }
        }
    }

    /**
     * Highlight squares for valid moves of the selected piece
     */
    private void highlightValidMoves(Canvas canvas) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Move move = new Move(chessBoard, selectedPiece, col, row);
                if (chessBoard.isValidMove(move)) {
                    float left = col * tileSize;
                    float top = row * tileSize;
                    float right = left + tileSize;
                    float bottom = top + tileSize;
                    canvas.drawRect(left, top, right, bottom, highlightPaint);
                }
            }
        }
    }

    /**
     * Draw game over message
     */
    private void drawGameOverMessage(Canvas canvas) {
        int x = getWidth() / 2;
        int y = getHeight() / 2;

        // Draw semi-transparent background
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#99000000"));
        canvas.drawRect(0, y - 80, getWidth(), y + 80, bgPaint);

        // Draw text
        textPaint.setColor(Color.WHITE);
        canvas.drawText(gameResult, x, y, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Convert touch coordinates to board position
        int col = (int) (event.getX() / tileSize);
        int row = (int) (event.getY() / tileSize);

        // Check if position is within the board
        if (col < 0 || col > 7 || row < 0 || row > 7) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return handleTouchDown(col, row, event.getX(), event.getY());
            case MotionEvent.ACTION_MOVE:
                return handleTouchMove(event);
            case MotionEvent.ACTION_UP:
                return handleTouchUp(col, row);
            default:
                return false;
        }
    }

    /**
     * Handle the initial touch (piece selection)
     */
    private boolean handleTouchDown(int col, int row, float x, float y) {
        // Check if game is over
        if (isGameOver) {
            // Reset game if tapped after game over
            setupBoard();
            invalidate();
            return true;
        }

        // Find the piece at the touch position
        Piece piece = chessBoard.getPiece(col, row);

        // Only select pieces of the current player's color
        if (piece != null && piece.isWhite == isWhiteToMove) {
            selectedPiece = piece;
            draggingPiece = true;

            // Center the piece on the touch point for more intuitive dragging
            selectedPiece.xPos = (int)(x - tileSize / 2);
            selectedPiece.yPos = (int)(y - tileSize / 2);

            invalidate();
            return true;
        }

        return false;
    }

    /**
     * Handle dragging of selected piece
     */
    private boolean handleTouchMove(MotionEvent event) {
        if (selectedPiece != null && draggingPiece) {
            // Update the visual position for dragging
            selectedPiece.xPos = (int) (event.getX() - (tileSize / 2));
            selectedPiece.yPos = (int) (event.getY() - (tileSize / 2));

            invalidate();
            return true;
        }

        return false;
    }

    /**
     * Handle releasing the piece (making a move)
     */
    private boolean handleTouchUp(int col, int row) {
        if (selectedPiece != null && draggingPiece) {
            draggingPiece = false;

            // Create and validate the move
            Move move = new Move(chessBoard, selectedPiece, col, row);
            if (chessBoard.isValidMove(move)) {
                // Make the move
                chessBoard.makeMove(move);

                // Switch turns
                isWhiteToMove = !isWhiteToMove;

                // Check for checkmate or stalemate
                checkGameStatus();
            } else {
                // Invalid move, return piece to original position
                selectedPiece.xPos = selectedPiece.col * tileSize;
                selectedPiece.yPos = selectedPiece.row * tileSize;
            }

            selectedPiece = null;
            invalidate();
            return true;
        }

        return false;
    }

    /**
     * Check if the game has ended (checkmate or stalemate)
     */
    private void checkGameStatus() {
        // Find the king of the current player
        Piece king = chessBoard.findKing(isWhiteToMove);
        if (king != null) {
            // Check if the game is over
            if (chessBoard.getCheckScanner().isGameOver(king)) {
                isGameOver = true;

                // Check if king is in check (checkmate) or not (stalemate)
                Move dummyMove = new Move(chessBoard, king, king.col, king.row);
                if (chessBoard.getCheckScanner().isKingInCheck(dummyMove)) {
                    gameResult = isWhiteToMove ? "Black wins by checkmate!" : "White wins by checkmate!";
                } else {
                    gameResult = "Draw by stalemate!";
                }

                Toast.makeText(getContext(), gameResult, Toast.LENGTH_LONG).show();
            }
        }
    }

    public int getTileSize() {
        return 0;
    }
}