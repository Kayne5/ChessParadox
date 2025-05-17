package com.example.ChessParadox.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ChessParadox.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Main activity for the Chess Paradox app
 * Serves as the entry point and game mode selection screen
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ChessParadox";
    private static final String SHARED_PREF_NAME = "chessParadoxPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private SharedPreferences sharedPreferences;

    // Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set to fullscreen for better visual experience
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // Check if user is logged in with Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_main);

        // Handle the tap anywhere to play functionality
        View tapableArea = findViewById(R.id.tapableArea);
        if (tapableArea != null) {
            tapableArea.setOnClickListener(v -> navigateToGameModes());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            redirectToLogin();
        }
    }

    /**
     * Redirect user to login screen if not authenticated
     */
    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        startActivity(intent);
        finish();  // Close this activity so user can't go back without logging in
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

        // Add logout button
        Button logoutButton = findViewById(R.id.logoutButton);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> logoutUser());
        }

        // Set up game mode selection buttons
        setupGameModeSelections();

        // Display welcome message with username from Firebase
        FirebaseUser user = mAuth.getCurrentUser();
        String username = user.getDisplayName();
        if (username == null || username.isEmpty()) {
            // If no display name set, use email before @ symbol
            username = user.getEmail().split("@")[0];
        }
        Toast.makeText(this, "Welcome, " + username + "!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Log out the current user using Firebase
     */
    private void logoutUser() {
        // Sign out from Firebase
        mAuth.signOut();

        // Clear login status in SharedPreferences too
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(KEY_USERNAME);
        editor.apply();

        Toast.makeText(MainActivity.this, "You have been logged out", Toast.LENGTH_SHORT).show();
        redirectToLogin();
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
            case "CLASSIC":
            default:
                gameIntent = new Intent(this, ClassicChessActivity.class);
                break;
        }

        // Pass the game mode to the activity
        gameIntent.putExtra("GAME_MODE", gameMode);

        // Get username from Firebase user
        FirebaseUser user = mAuth.getCurrentUser();
        String username = user.getDisplayName();
        if (username == null || username.isEmpty()) {
            // If no display name set, use email before @ symbol
            username = user.getEmail().split("@")[0];
        }
        gameIntent.putExtra("USERNAME", username);

        startActivity(gameIntent);
    }
}