package com.example.ChessParadox.Classic;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.ChessParadox.R;
import com.example.ChessParadox.Pieces.Piece;

/**
 * Dialog to let the player choose which piece to promote a pawn to
 */
public class PawnPromotionDialog extends Dialog {
    private final Piece pawn;
    private final Chessboard chessboard;
    private final OnPromotionListener listener;
    private final boolean isWhite;

    public interface OnPromotionListener {
        void onPieceSelected(String pieceType);
    }

    public PawnPromotionDialog(Context context, Piece pawn, Chessboard chessboard, OnPromotionListener listener) {
        super(context);
        this.pawn = pawn;
        this.chessboard = chessboard;
        this.listener = listener;
        this.isWhite = pawn.isWhite;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_pawn_promotion);
        setCancelable(false); // Force player to choose a promotion

        // Find the promotion options layout
        LinearLayout optionsLayout = findViewById(R.id.promotion_options_layout);

        // Create buttons for each promotion option
        String[] pieceTypes = {"Queen", "Rook", "Bishop", "Knight"};

        // Use a more appropriate button size - not too large
        int buttonSize = Math.min(chessboard.tileSize, 120); // Limit maximum size
        Context context = getContext();

        for (final String pieceType : pieceTypes) {
            ImageButton pieceBtn = new ImageButton(context);

            // Get the appropriate resource ID for this piece
            String colorPrefix = isWhite ? "white" : "black";
            String resourceName = colorPrefix + "_" + pieceType.toLowerCase();
            int resourceId = context.getResources().getIdentifier(
                    resourceName, "drawable", context.getPackageName());

            // Load and scale the image to match the tile size
            Bitmap originalBitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
            if (originalBitmap != null) {
                // Create a properly scaled bitmap that matches our button size
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, buttonSize, buttonSize, true);
                pieceBtn.setImageBitmap(scaledBitmap);
                // Recycle the original bitmap to free memory
                originalBitmap.recycle();
            } else {
                // Fallback if bitmap loading fails
                pieceBtn.setImageResource(resourceId);
            }

            pieceBtn.setScaleType(ImageButton.ScaleType.FIT_CENTER);
            pieceBtn.setBackgroundColor(Color.TRANSPARENT);
            pieceBtn.setPadding(8, 8, 8, 8); // Add some padding

            // Set layout parameters
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(buttonSize, buttonSize);
            params.setMargins(10, 0, 10, 0);
            pieceBtn.setLayoutParams(params);

            // Set click listener
            pieceBtn.setOnClickListener(v -> {
                listener.onPieceSelected(pieceType);
                dismiss();
            });

            // Add to the options layout
            optionsLayout.addView(pieceBtn);
        }

        // Set dialog size to wrap content properly but limit width
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}