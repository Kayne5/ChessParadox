package com.example.ChessParadox.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ChessParadox.Classic.GameModesActivity;
import com.example.ChessParadox.R;

/**
 * Start page activity that shows the initial screen when the app launches
 */
public class StartPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set to fullscreen for better visual experience
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_start_page);

        // Find the view that covers the entire screen for tap detection
        View tapableArea = findViewById(R.id.tapableArea);

        // Set click listener to navigate to game modes screen when tapped
        tapableArea.setOnClickListener(v -> {
            Intent intent = new Intent(StartPageActivity.this, GameModesActivity.class);
            startActivity(intent);
        });
    }
}