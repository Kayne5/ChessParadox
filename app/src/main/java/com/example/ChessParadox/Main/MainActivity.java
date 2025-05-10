package com.example.ChessParadox.Main;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ChessParadox.Classic.CapturedPiecesView;
import com.example.ChessParadox.Classic.Chessboardview;
import com.example.ChessParadox.R;

/**
 * Main activity for the Chess Paradox game
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ChessApp";
    private Chessboardview chessboardview;
    private CapturedPiecesView capturedPiecesViewTop;
    private CapturedPiecesView capturedPiecesViewBottom;
    private String gameMode = "CLASSIC"; // Default game mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Set to fullscreen for better gameplay experience
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );

            setContentView(R.layout.activity_main);

            // Get the game mode from the intent if available
            if (getIntent().hasExtra("GAME_MODE")) {
                gameMode = getIntent().getStringExtra("GAME_MODE");
            }

            Log.d(TAG, "Starting game with mode: " + gameMode);

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
            } else {
                Log.w(TAG, "Chess board not initialized yet");
            }

            Log.d(TAG, "Chess game initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing chess game", e);
            Toast.makeText(this, "Error initializing game: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
     * Reset the game state when needed
     */
    private void resetGame() {
        if (chessboardview != null) {
            // This will also reset captured pieces views through the chessboard reference
            chessboardview.setupBoard();
            chessboardview.invalidate();
        }
    }
}