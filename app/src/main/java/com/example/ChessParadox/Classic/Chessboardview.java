package com.example.ChessParadox.Classic;

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
    private Paint selectedPiecePaint;  // New paint for highlighting selected piece
    private Paint checkHighlightPaint; // New paint for highlighting king in check
    private Paint textPaint;

    // Drag and drop
    private float touchX, touchY;
    private boolean draggingPiece = false;

    // Game logic
    private Chessboard chessBoard;
    public Piece selectedPiece;
    private boolean pieceSelected = false;  // Track if a piece is selected for click-and-move

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

        // New paint for highlighting the selected piece
        selectedPiecePaint = new Paint();
        selectedPiecePaint.setColor(Color.parseColor("#55FF8800"));
        selectedPiecePaint.setAlpha(180);

        // New paint for highlighting king in check
        checkHighlightPaint = new Paint();
        checkHighlightPaint.setColor(Color.parseColor("#FFFF0000")); // Red for check
        checkHighlightPaint.setAlpha(180);

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
        pieceSelected = false;
    }

    private void addPiece(Piece piece) {
        pieceList.add(piece);
    }

    /**
     * Check if a king is in check
     */
    private boolean isKingInCheck(boolean isWhiteKing) {
        Piece king = chessBoard.findKing(isWhiteKing);
        if (king != null) {
            Move dummyMove = new Move(chessBoard, king, king.col, king.row);
            return chessBoard.getCheckScanner().isKingInCheck(dummyMove);
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the chess board
        drawBoard(canvas);

        // Highlight kings in check
        highlightKingsInCheck(canvas);

        // Highlight the selected piece's square if using click-and-move
        if (selectedPiece != null && pieceSelected && !draggingPiece) {
            float left = selectedPiece.col * tileSize;
            float top = selectedPiece.row * tileSize;
            float right = left + tileSize;
            float bottom = top + tileSize;
            canvas.drawRect(left, top, right, bottom, selectedPiecePaint);
        }

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
     * Highlight kings that are in check
     */
    private void highlightKingsInCheck(Canvas canvas) {
        // Check if white king is in check
        if (isKingInCheck(true)) {
            Piece whiteKing = chessBoard.findKing(true);
            if (whiteKing != null) {
                float left = whiteKing.col * tileSize;
                float top = whiteKing.row * tileSize;
                float right = left + tileSize;
                float bottom = top + tileSize;
                canvas.drawRect(left, top, right, bottom, checkHighlightPaint);
            }
        }

        // Check if black king is in check
        if (isKingInCheck(false)) {
            Piece blackKing = chessBoard.findKing(false);
            if (blackKing != null) {
                float left = blackKing.col * tileSize;
                float top = blackKing.row * tileSize;
                float right = left + tileSize;
                float bottom = top + tileSize;
                canvas.drawRect(left, top, right, bottom, checkHighlightPaint);
            }
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

        // Get the action
        int action = event.getAction();

        // Check if game is over
        if (isGameOver && action == MotionEvent.ACTION_DOWN) {
            // Reset game if tapped after game over
            setupBoard();
            invalidate();
            return true;
        }

        // Detect if this is a possible drag gesture or just a click
        if (action == MotionEvent.ACTION_DOWN) {
            touchX = event.getX();
            touchY = event.getY();
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            // Only start dragging if the move is significant
            float dx = Math.abs(event.getX() - touchX);
            float dy = Math.abs(event.getY() - touchY);

            if (dx > tileSize / 10 || dy > tileSize / 10) {
                // User is dragging, so handle drag-and-drop
                if (selectedPiece != null && !draggingPiece && pieceSelected) {
                    draggingPiece = true;
                    selectedPiece.xPos = (int)(event.getX() - tileSize / 2);
                    selectedPiece.yPos = (int)(event.getY() - tileSize / 2);
                    invalidate();
                    return true;
                }

                // If no piece is selected yet, check if we should select one now
                if (!pieceSelected && !draggingPiece) {
                    Piece piece = chessBoard.getPiece((int)(touchX / tileSize), (int)(touchY / tileSize));
                    if (piece != null && piece.isWhite == isWhiteToMove) {
                        selectedPiece = piece;
                        pieceSelected = true;
                        draggingPiece = true;
                        selectedPiece.xPos = (int)(event.getX() - tileSize / 2);
                        selectedPiece.yPos = (int)(event.getY() - tileSize / 2);
                        invalidate();
                        return true;
                    }
                }
            }

            if (draggingPiece) {
                // Continue dragging
                selectedPiece.xPos = (int)(event.getX() - tileSize / 2);
                selectedPiece.yPos = (int)(event.getY() - tileSize / 2);
                invalidate();
                return true;
            }
        } else if (action == MotionEvent.ACTION_UP) {
            // If we were dragging, handle the drop
            if (draggingPiece) {
                draggingPiece = false;
                if (selectedPiece != null) {
                    handleMove(col, row);
                }
                return true;
            }

            // If we weren't dragging, handle click-and-move interaction
            if (!draggingPiece) {
                handleClickAndMove(col, row);
                return true;
            }
        }

        return false;
    }

    /**
     * Handle the click-and-move interaction
     */
    private void handleClickAndMove(int col, int row) {
        Piece clickedPiece = chessBoard.getPiece(col, row);

        // If no piece is selected and the user clicked a valid piece, select it
        if (!pieceSelected && clickedPiece != null && clickedPiece.isWhite == isWhiteToMove) {
            selectedPiece = clickedPiece;
            pieceSelected = true;
            invalidate();
            return;
        }

        // If a piece is already selected
        if (pieceSelected && selectedPiece != null) {
            // If the user clicked on another of their pieces, switch selection
            if (clickedPiece != null && clickedPiece.isWhite == isWhiteToMove) {
                selectedPiece = clickedPiece;
                invalidate();
                return;
            }

            // Otherwise, try to move to the clicked square
            handleMove(col, row);
        }
    }

    /**
     * Attempt to move the selected piece to the specified position
     */
    private void handleMove(int col, int row) {
        // Create and validate the move
        Move move = new Move(chessBoard, selectedPiece, col, row);
        if (chessBoard.isValidMove(move)) {
            // Make the move
            chessBoard.makeMove(move);

            // Update the visual position of all pieces
            updatePiecesVisualPosition();

            // Switch turns
            isWhiteToMove = !isWhiteToMove;

            // Check for checkmate or stalemate
            checkGameStatus();
        } else {
            // Invalid move, return piece to original position
            selectedPiece.xPos = selectedPiece.col * tileSize;
            selectedPiece.yPos = selectedPiece.row * tileSize;
        }

        // Reset selection
        selectedPiece = null;
        pieceSelected = false;
        invalidate();
    }

    /**
     * Update the visual position (xPos, yPos) of all pieces based on their logical position (col, row)
     */
    private void updatePiecesVisualPosition() {
        for (Piece piece : pieceList) {
            piece.xPos = piece.col * tileSize;
            piece.yPos = piece.row * tileSize;
        }
    }

    /**
     * Check if the game has ended (checkmate or stalemate)
     */
    private void checkGameStatus() {
        // Find the king of the current player
        Piece king = chessBoard.findKing(isWhiteToMove);
        if (king != null) {
            // Check if king is in check and show toast notification
            Move dummyMove = new Move(chessBoard, king, king.col, king.row);
            if (chessBoard.getCheckScanner().isKingInCheck(dummyMove)) {
                Toast.makeText(getContext(), (isWhiteToMove ? "White" : "Black") + " king is in check!", Toast.LENGTH_SHORT).show();
            }

            // Check if the game is over
            if (chessBoard.getCheckScanner().isGameOver(king)) {
                isGameOver = true;

                // Check if king is in check (checkmate) or not (stalemate)
                if (chessBoard.getCheckScanner().isKingInCheck(dummyMove)) {
                    gameResult = isWhiteToMove ? "Black wins by checkmate!" : "White wins by checkmate!";
                } else {
                    gameResult = "Draw by stalemate!";
                }

                Toast.makeText(getContext(), gameResult, Toast.LENGTH_LONG).show();
            }
        }

        // Force a redraw to update king highlight if needed
        invalidate();
    }

    public int getTileSize() {
        return tileSize;
    }
}