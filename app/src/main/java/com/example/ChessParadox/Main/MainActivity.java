package com.example.ChessParadox.Main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ChessParadox.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ChessApp";
    private Chessboardview chessboardview;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "Setting content view");
            setContentView(R.layout.activity_main);

            Log.d(TAG, "Finding chessboardview");
            chessboardview = findViewById(R.id.chessboardview);

            if (chessboardview == null) {
                Log.e(TAG, "Chessboardview not found in layout");
                Toast.makeText(this, "Error: Chess board not found", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "Chessboardview initialized successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing app", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}