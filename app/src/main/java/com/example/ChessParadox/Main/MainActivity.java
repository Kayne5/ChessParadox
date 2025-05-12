package com.example.ChessParadox.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import com.example.ChessParadox.R;

/**
 * Main activity for the Chess Paradox app
 * Serves as the entry point and game mode selection screen
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ChessParadox";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set to fullscreen for better visual experience
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_main);

        // Handle the tap anywhere to play functionality
        View tapableArea = findViewById(R.id.tapableArea);
        if (tapableArea != null) {
            tapableArea.setOnClickListener(v -> navigateToGameModes());
        }
    }

    /**
     * Navigate to the game modes selection screen
     */
    private void navigateToGameModes() {
        setContentView(R.layout.activity_game_modes);

        // Set up the back button on game modes screen
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                // Return to the main screen
                setContentView(R.layout.activity_main);
                // Re-setup the tap anywhere functionality
                View tapableArea = findViewById(R.id.tapableArea);
                if (tapableArea != null) {
                    tapableArea.setOnClickListener(v2 -> navigateToGameModes());
                }
            });
        }

        // Set up game mode selection buttons
        setupGameModeSelections();
    }

    /**
     * Set up the game mode selection cards and their click listeners
     */
    private void setupGameModeSelections() {
        // Classic Chess mode
        Button classicChessButton = findViewById(R.id.playClassicChessButton);
        if (classicChessButton != null) {
            classicChessButton.setOnClickListener(v -> startChessGame("CLASSIC"));
        }

        // Two Steps Ahead mode
        Button twoStepsButton = findViewById(R.id.playTwoStepsButton);
        if (twoStepsButton != null) {
            twoStepsButton.setOnClickListener(v -> startChessGame("TWO_STEPS"));
        }

        // Fog of War mode - commented out for now
        Button fogOfWarButton = findViewById(R.id.playFogOfWarButton);
        if (fogOfWarButton != null) {
            // Disable the button since Fog of War is not implemented yet
            fogOfWarButton.setEnabled(false);
            fogOfWarButton.setText("Coming Soon");

            // Optional: Show a toast when clicked
            fogOfWarButton.setOnClickListener(v ->
                    Toast.makeText(this, "Fog of War mode coming soon!", Toast.LENGTH_SHORT).show()
            );

            // Alternative: Hide the entire container
            // View fogOfWarContainer = findViewById(R.id.fogOfWarContainer);
            // if (fogOfWarContainer != null) {
            //     fogOfWarContainer.setVisibility(View.GONE);
            // }
        }
    }

    /**
     * Start the appropriate chess game activity based on selected mode
     * @param gameMode The selected game mode
     */
    private void startChessGame(String gameMode) {
        Intent gameIntent;

        switch (gameMode) {
            case "TWO_STEPS":
                gameIntent = new Intent(this, TwoStepChessActivity.class);
                break;
            // Fog of War commented out for now
            // case "FOG_OF_WAR":
            //     gameIntent = new Intent(this, ClassicChessActivity.class);
            //     break;
            case "CLASSIC":
            default:
                gameIntent = new Intent(this, ClassicChessActivity.class);
                break;
        }

        // Pass the game mode to the activity
        gameIntent.putExtra("GAME_MODE", gameMode);
        startActivity(gameIntent);
    }
}