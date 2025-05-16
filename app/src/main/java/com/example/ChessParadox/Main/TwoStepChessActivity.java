package com.example.ChessParadox.Main;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ChessParadox.Classic.CapturedPiecesView;
import com.example.ChessParadox.R;
import com.example.ChessParadox.TwoStepsAhead.TwoStepChessboardView;


/**
 * Activity that displays and handles the Two-Steps Ahead chess variant
 */
public class TwoStepChessActivity extends AppCompatActivity {

    private static final String TAG = "ChessApp";
    private TwoStepChessboardView twostepchessboardview;
    private CapturedPiecesView capturedPiecesViewTop;
    private CapturedPiecesView capturedPiecesViewBottom;
    private TextView titleText;
    private TextView gameStatusText;
    private boolean isWhiteTurn = true;
    private boolean isSecondMove = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Set to fullscreen for better visual experience
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );

            setContentView(R.layout.activity_two_step_chess);

            // Initialize UI components
            twostepchessboardview = findViewById(R.id.twostepchessboardview);
            capturedPiecesViewTop = findViewById(R.id.capturedPiecesViewTop);
            capturedPiecesViewBottom = findViewById(R.id.capturedPiecesViewBottom);
            titleText = findViewById(R.id.titleText);
            // Add game status text view if needed
            // gameStatusText = findViewById(R.id.gameStatusText);

            if (twostepchessboardview == null) {
                throw new IllegalStateException("Two-step chessboard view not found in layout");
            }

            // Set up back button
            ImageView backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(v -> onBackPressed());

            // Set orientation for captured pieces views
            if (capturedPiecesViewTop != null) {
                capturedPiecesViewTop.setOrientation(false); // horizontal layout
            }

            if (capturedPiecesViewBottom != null) {
                capturedPiecesViewBottom.setOrientation(false); // horizontal layout
            }

            // Connect the board with the captured pieces views
            if (twostepchessboardview.twostepchessBoard != null) {
                twostepchessboardview.twostepchessBoard.setCapturedPiecesViews(capturedPiecesViewTop, capturedPiecesViewBottom);

                // Set up callbacks for move completion to update status
                setupMoveCallbacks();
            } else {
                Log.w(TAG, "Two-step chess board not initialized yet");
            }

            // Find and set up the reset button
            ImageView resetButton = findViewById(R.id.resetButton);
            if (resetButton != null) {
                resetButton.setOnClickListener(v -> resetGame());
            }

            // Initialize game
            setupGame();
            Log.d(TAG, "Two-step chess game initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing two-step chess game", e);
            Toast.makeText(this, "Error initializing game: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Set up callbacks to be notified when moves are completed
     */
    private void setupMoveCallbacks() {
        // We can use the board's state to track moves
        // Since TwoStepChessboardView already handles the logic internally,
        // we can simply update UI elements here if needed
    }

    /**
     * Set up the initial game state
     */
    private void setupGame() {
        // Reset game state variables
        isWhiteTurn = true;
        isSecondMove = false;

        // Set game info text
        updateGameStatus(isWhiteTurn, isSecondMove);

        // Reset the board
        if (twostepchessboardview != null) {
            twostepchessboardview.resetGame();
        }
    }

    /**
     * Update the game status text
     * @param isWhiteTurn whether it's white's turn
     * @param isSecondMove whether it's the second move of the turn
     */
    public void updateGameStatus(boolean isWhiteTurn, boolean isSecondMove) {
        String playerText = isWhiteTurn ? "White" : "Black";
        String moveText = isSecondMove ? "second" : "first";

        String statusText = playerText + "'s turn - Make your " + moveText + " move";

        // If we have a status text view, update it
        if (gameStatusText != null) {
            gameStatusText.setText(statusText);
        } else {
            // Otherwise just show a toast
            Toast.makeText(this, statusText, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle reset game action
     */
    public void resetGame() {
        setupGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Make sure view is refreshed when activity comes to foreground
        if (twostepchessboardview != null) {
            twostepchessboardview.invalidate();
        }

        // Also refresh captured pieces views
        if (capturedPiecesViewTop != null) {
            capturedPiecesViewTop.invalidate();
        }

        if (capturedPiecesViewBottom != null) {
            capturedPiecesViewBottom.invalidate();
        }
    }
}