package com.example.ChessParadox.Main;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ChessParadox.R;

/**
 * Main activity for the Chess Paradox game
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ChessApp";
    private Chessboardview chessboardview;

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

            // Find and initialize the chess board view
            chessboardview = findViewById(R.id.chessboardview);

            if (chessboardview == null) {
                throw new IllegalStateException("Chessboard view not found in layout");
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
    }
}