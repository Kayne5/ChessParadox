package com.example.ChessParadox.Main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ChessParadox.Classic.CapturedPiecesView;
import com.example.ChessParadox.Classic.Chessboardview;
import com.example.ChessParadox.R;

/**
 * Activity for the Classic Chess game mode
 */
public class ClassicChessActivity extends AppCompatActivity {

    private static final String TAG = "ChessApp";
    private Chessboardview chessboardview;
    private CapturedPiecesView capturedPiecesViewTop;
    private CapturedPiecesView capturedPiecesViewBottom;
    private String gameMode = "CLASSIC"; // Default game mode
    private TextView gameStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Set to fullscreen for better gameplay experience
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );

            setContentView(R.layout.activity_classic_chess);

            // Get the game mode from the intent if available
            if (getIntent().hasExtra("GAME_MODE")) {
                gameMode = getIntent().getStringExtra("GAME_MODE");
            }

            Log.d(TAG, "Starting game with mode: " + gameMode);

            // Set up the back button
            ImageView backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(v -> onBackPressed());

            // Update title based on game mode
            TextView titleText = findViewById(R.id.titleText);
            if (titleText != null) {
                if ("TWO_STEPS".equals(gameMode)) {
                    titleText.setText("TWO STEPS AHEAD");
                } else if ("FOG_OF_WAR".equals(gameMode)) {
                    titleText.setText("FOG OF WAR");
                } else {
                    titleText.setText("CLASSICAL CHESS");
                }
            }

            // Find and initialize views
            chessboardview = findViewById(R.id.chessboardview);
            capturedPiecesViewTop = findViewById(R.id.capturedPiecesViewTop);
            capturedPiecesViewBottom = findViewById(R.id.capturedPiecesViewBottom);

            if (chessboardview == null) {
                throw new IllegalStateException("Chessboard view not found in layout");
            }

            // Set orientation for captured pieces views
            if (capturedPiecesViewTop != null) {
                capturedPiecesViewTop.setOrientation(false); // horizontal layout
            }

            if (capturedPiecesViewBottom != null) {
                capturedPiecesViewBottom.setOrientation(false); // horizontal layout
            }

            // Connect the captured pieces views to the chessboard
            if (chessboardview.chessBoard != null) {
                chessboardview.chessBoard.setCapturedPiecesViews(capturedPiecesViewTop, capturedPiecesViewBottom);

                // Set up game status update listener if this is fog of war mode
                if ("FOG_OF_WAR".equals(gameMode)) {
                    setupFogOfWarMode();
                }
            } else {
                Log.w(TAG, "Chess board not initialized yet");
            }

            // Find and set up the reset button
            ImageView resetButton = findViewById(R.id.resetButton);
            if (resetButton != null) {
                resetButton.setOnClickListener(v -> resetGame());
            }

            updateGameStatus("White's turn");
            Log.d(TAG, "Chess game initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing chess game", e);
            Toast.makeText(this, "Error initializing game: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Set up fog of war specific settings
     * Note: This is a placeholder for future implementation
     */
    private void setupFogOfWarMode() {
        // This would be implemented when Fog of War mode is fully developed
        Toast.makeText(this, "Fog of War mode is under development", Toast.LENGTH_SHORT).show();
    }

    /**
     * Update the game status text
     * @param status the current game status to display
     */
    public void updateGameStatus(String status) {
        if (gameStatusText != null) {
            gameStatusText.setText(status);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Make sure view is refreshed when activity comes to foreground
        if (chessboardview != null) {
            chessboardview.invalidate();
        }

        // Also refresh captured pieces views
        if (capturedPiecesViewTop != null) {
            capturedPiecesViewTop.invalidate();
        }

        if (capturedPiecesViewBottom != null) {
            capturedPiecesViewBottom.invalidate();
        }
    }

    /**
     * Reset the game state
     */
    public void resetGame() {
        if (chessboardview != null) {
            // This will also reset captured pieces views through the chessboard reference
            chessboardview.setupBoard();
            chessboardview.invalidate();
            updateGameStatus("White's turn");
        }
    }
}