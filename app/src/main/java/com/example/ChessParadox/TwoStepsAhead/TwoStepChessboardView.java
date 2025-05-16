package com.example.ChessParadox.TwoStepsAhead;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.ChessParadox.Classic.Chessboardview;
import com.example.ChessParadox.Classic.Move;
import com.example.ChessParadox.Main.TwoStepChessActivity;
import com.example.ChessParadox.Pieces.Piece;

/**
 * Custom view for the Two-Steps Ahead chess variant
 * Extends the classic chessboard view with additional turn-based mechanics
 */
public class TwoStepChessboardView extends Chessboardview {

    // Reference to the specific board for this variant
    public TwoStepChessboard twostepchessBoard;

    // UI colors for highlighting first-move pieces
    private Paint firstMovePieceHighlight;

    // Game state
    private boolean gameOver = false;
    private String gameOverMessage = "";

    public TwoStepChessboardView(Context context) {
        super(context);
        initializeBoard();
    }

    public TwoStepChessboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeBoard();
    }

    public TwoStepChessboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeBoard();
    }

    @Override
    protected void initializeBoard() {
        // Initialize the two-step chessboard
        twostepchessBoard = new TwoStepChessboard(this);
        chessBoard = twostepchessBoard;  // Set the parent's board to our variant

        // Initialize color for highlighting the first-moved piece
        firstMovePieceHighlight = new Paint();
        firstMovePieceHighlight.setColor(Color.parseColor("#80FFA500"));  // Orange with transparency
        firstMovePieceHighlight.setAlpha(180);

        // Call parent's initialization if needed
        super.initializeBoard();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw additional information for Two-Steps Ahead variant

        // Highlight the piece that moved in the first move
        Piece firstMovePiece = twostepchessBoard.getFirstMovePiece();
        if (firstMovePiece != null && !twostepchessBoard.isFirstMove()) {
            float left = indicatorSize + firstMovePiece.col * tileSize;
            float top = indicatorSize + firstMovePiece.row * tileSize;
            float right = left + tileSize;
            float bottom = top + tileSize;
            canvas.drawRect(left, top, right, bottom, firstMovePieceHighlight);
        }

        // Show turn indicator text at the top
        if (!gameOver) {
            Paint turnTextPaint = new Paint();
            turnTextPaint.setColor(Color.WHITE);
            turnTextPaint.setTextSize(tileSize / 3);
            turnTextPaint.setTextAlign(Paint.Align.CENTER);

            String turnText = (twostepchessBoard.isWhiteTurn() ? "White" : "Black") + "'s turn";
            String moveText = " - " + (twostepchessBoard.isFirstMove() ? "First" : "Second") + " move";

            canvas.drawText(turnText + moveText,
                    getWidth() / 2,
                    indicatorSize / 2 + turnTextPaint.getTextSize() / 3,
                    turnTextPaint);
        }

        // Draw game over message if game is over
        if (gameOver) {
            Paint gameOverPaint = new Paint();
            gameOverPaint.setColor(Color.parseColor("#99000000"));  // Semi-transparent black

            float boxHeight = tileSize * 2;
            float boxTop = (getHeight() - boxHeight) / 2;

            canvas.drawRect(0, boxTop, getWidth(), boxTop + boxHeight, gameOverPaint);

            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(tileSize / 2);
            textPaint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText(gameOverMessage,
                    getWidth() / 2,
                    boxTop + boxHeight / 2 + textPaint.getTextSize() / 3,
                    textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If game is over, handle reset on touch
        if (gameOver && event.getAction() == MotionEvent.ACTION_DOWN) {
            resetGame();
            return true;
        }

        // Pass to parent handler for normal chess logic
        return super.onTouchEvent(event);
    }

    /**
     * Update the turn state in the UI and parent activity
     */
    public void updateTurnState(boolean isWhiteTurn, boolean isFirstMove) {
        // Update the parent activity if possible
        if (getContext() instanceof TwoStepChessActivity) {
            ((TwoStepChessActivity) getContext()).updateGameStatus(isWhiteTurn, !isFirstMove);
        }

        // Redraw the board
        invalidate();
    }

    /**
     * Handle move attempt from user
     */
    @Override
    protected void handleMove(int col, int row) {
        if (selectedPiece == null) {
            return;
        }

        // Check if this is a valid move
        if (selectedPiece.isWhite == twostepchessBoard.isWhiteTurn()) {
            Move move = new Move(twostepchessBoard, selectedPiece, col, row);

            if (twostepchessBoard.isValidMove(move)) {
                // Make the move
                twostepchessBoard.makeMove(move);

                // Update visual positions of all pieces
                updatePiecesVisualPosition();

                // Check for game over conditions
                checkGameStatus();
            } else {
                // Invalid move, return piece to original position
                selectedPiece.xPos = indicatorSize + selectedPiece.col * tileSize;
                selectedPiece.yPos = indicatorSize + selectedPiece.row * tileSize;

                // Show a toast explaining why the move is invalid
                if (twostepchessBoard.isFirstMove() && selectedPiece.isWhite != twostepchessBoard.isWhiteTurn()) {
                    Toast.makeText(getContext(), "It's " + (twostepchessBoard.isWhiteTurn() ? "white" : "black") + "'s turn", Toast.LENGTH_SHORT).show();
                } else if (!twostepchessBoard.isFirstMove() && selectedPiece == twostepchessBoard.getFirstMovePiece()) {
                    Toast.makeText(getContext(), "Can't move the same piece twice in one turn", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // Wrong player's piece
            Toast.makeText(getContext(), "It's " + (twostepchessBoard.isWhiteTurn() ? "white" : "black") + "'s turn", Toast.LENGTH_SHORT).show();

            // Return piece to original position
            selectedPiece.xPos = indicatorSize + selectedPiece.col * tileSize;
            selectedPiece.yPos = indicatorSize + selectedPiece.row * tileSize;
        }

        // Reset selection
        selectedPiece = null;
        pieceSelected = false;
        invalidate();
    }

    /**
     * Check if the game has ended
     */
    private void checkGameStatus() {
        // Only check game status after the second move
        if (twostepchessBoard.isFirstMove()) {
            // Find the king of the current player (who just completed their turn)
            boolean currentPlayerIsWhite = !twostepchessBoard.isWhiteTurn();
            Piece king = twostepchessBoard.findKing(currentPlayerIsWhite);

            if (king != null) {
                // Check if the king of the current player is in check
                Move dummyMove = new Move(twostepchessBoard, king, king.col, king.row);
                if (twostepchessBoard.getCheckScanner().isKingInCheck(dummyMove)) {
                    String playerName = currentPlayerIsWhite ? "White" : "Black";
                    Toast.makeText(getContext(), playerName + " king is in check!", Toast.LENGTH_SHORT).show();
                }

                // Check for checkmate or stalemate
                if (twostepchessBoard.getCheckScanner().isGameOver(king)) {
                    gameOver = true;

                    // Determine if it's checkmate or stalemate
                    if (twostepchessBoard.getCheckScanner().isKingInCheck(dummyMove)) {
                        gameOverMessage = currentPlayerIsWhite ? "Black wins by checkmate!" : "White wins by checkmate!";
                    } else {
                        gameOverMessage = "Draw by stalemate!";
                    }

                    Toast.makeText(getContext(), gameOverMessage, Toast.LENGTH_LONG).show();
                }
            }
        }

        // Force redraw
        invalidate();
    }

    /**
     * Reset the game
     */
    @Override
    public void resetGame() {
        super.resetGame();

        // Reset the game state
        gameOver = false;
        gameOverMessage = "";

        // Reset the two-step chess state
        twostepchessBoard.reset();

        // Reset the first move piece
        twostepchessBoard.resetFirstMovePiece();

        // Redraw
        invalidate();
    }
}