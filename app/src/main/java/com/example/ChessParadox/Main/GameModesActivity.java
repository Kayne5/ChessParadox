package com.example.ChessParadox.Main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
            Intent intent = new Intent(GameModesActivity.this, ClassicChessActivity.class);
            startActivity(intent);
        });

        // Set up two-steps ahead mode
        Button playTwoStepsButton = findViewById(R.id.playTwoStepsButton);
        playTwoStepsButton.setOnClickListener(v -> {
            // Launch the dedicated TwoStepChessActivity
            Intent intent = new Intent(GameModesActivity.this, TwoStepChessActivity.class);
            startActivity(intent);
        });

        // Set up fog of war mode
        Button playFogOfWarButton = findViewById(R.id.playFogOfWarButton);
        playFogOfWarButton.setOnClickListener(v -> {
            // This would launch a different activity for the Fog of War mode
            // For now, we'll use the main activity with a parameter
            Intent intent = new Intent(GameModesActivity.this, ClassicChessActivity.class);
            intent.putExtra("GAME_MODE", "FOG_OF_WAR");
            startActivity(intent);
        });

        // Set up info icons to show game mode descriptions
        setupInfoIcons();
    }

    /**
     * Sets up click listeners for all info icons in the game modes screen
     */
    private void setupInfoIcons() {
        // Find all the LinearLayout containers
        View classicChessContainer = findViewById(R.id.classicChessContainer);
        View twoStepsContainer = findViewById(R.id.twoStepsContainer);
        View fogOfWarContainer = findViewById(R.id.fogOfWarContainer);

        // Find the info icons in each container (last child of each LinearLayout)
        ImageView classicInfoIcon = (ImageView) ((android.view.ViewGroup) classicChessContainer).getChildAt(2);
        ImageView twoStepsInfoIcon = (ImageView) ((android.view.ViewGroup) twoStepsContainer).getChildAt(2);
        ImageView fogOfWarInfoIcon = (ImageView) ((android.view.ViewGroup) fogOfWarContainer).getChildAt(2);

        // Set up click listeners
        classicInfoIcon.setOnClickListener(v -> showInfoDialog("Classic Chess",
                "Traditional chess game following standard rules. Play against the computer AI with " +
                        "adjustable difficulty levels. Perfect for both beginners and experienced players " +
                        "who want to improve their chess skills."));

        twoStepsInfoIcon.setOnClickListener(v -> showInfoDialog("Two-steps ahead",
                "An exciting variant where you make two moves per turn with different pieces. " +
                        "This creates new tactical possibilities and deepens strategic planning. " +
                        "Plan your moves carefully as you can't move the same piece twice in one turn!"));

        fogOfWarInfoIcon.setOnClickListener(v -> showInfoDialog("Fog of War",
                "An exciting variant where enemy pieces are only visible when they are in range of your " +
                        "pieces. This creates a thrilling gameplay experience that tests your memory and " +
                        "intuition. Be careful - danger could be lurking just out of sight!"));
    }

    /**
     * Shows a dialog with information about the selected game mode
     * @param title The game mode title
     * @param description Detailed description of the game mode
     */
    private void showInfoDialog(String title, String description) {
        // Create custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_game_info);

        // Set dialog title and description
        TextView titleText = dialog.findViewById(R.id.dialogTitle);
        TextView descriptionText = dialog.findViewById(R.id.dialogDescription);

        titleText.setText(title);
        descriptionText.setText(description);

        // Set up close button
        Button closeButton = dialog.findViewById(R.id.dialogCloseButton);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Show dialog
        dialog.show();
    }
}