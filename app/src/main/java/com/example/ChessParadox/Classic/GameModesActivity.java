package com.example.ChessParadox.Classic;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ChessParadox.Main.MainActivity;
import com.example.ChessParadox.R;

/**
 * Activity to display and select different game modes
 */
public class GameModesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set to fullscreen for better visual experience
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_game_modes);

        // Set up back button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // Set up classic chess mode
        Button playClassicButton = findViewById(R.id.playClassicChessButton);
        playClassicButton.setOnClickListener(v -> {
            Intent intent = new Intent(GameModesActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Set up two-steps ahead mode
        Button playTwoStepsButton = findViewById(R.id.playTwoStepsButton);
        playTwoStepsButton.setOnClickListener(v -> {
            // This would launch a different activity for the Two-steps ahead mode
            // For now, we'll use the main activity with a parameter
            Intent intent = new Intent(GameModesActivity.this, MainActivity.class);
            intent.putExtra("GAME_MODE", "TWO_STEPS");
            startActivity(intent);
        });

        // Set up fog of war mode
        Button playFogOfWarButton = findViewById(R.id.playFogOfWarButton);
        playFogOfWarButton.setOnClickListener(v -> {
            // This would launch a different activity for the Fog of War mode
            // For now, we'll use the main activity with a parameter
            Intent intent = new Intent(GameModesActivity.this, MainActivity.class);
            intent.putExtra("GAME_MODE", "FOG_OF_WAR");
            startActivity(intent);
        });
    }
}