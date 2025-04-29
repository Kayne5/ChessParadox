package com.example.ChessParadox.Main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.ChessParadox.Pieces.*;

import java.util.ArrayList;

public class Chessboardview extends View {

    private static final String TAG = "ChessboardView";
    public int tileSize;
    public ArrayList<Piece> pieceList = new ArrayList<>();
    public Piece selectedPiece;
    public CheckScanner checkScanner;
    public int enPassantTile = -1;

    private boolean isWhiteToMove = true;
    private boolean isGameOver = false;

    private Paint lightTilePaint;
    private Paint darkTilePaint;
    private Paint highlightPaint;

    private Chessboard chessBoard;

    public Chessboardview(Context context) {
        super(context);
        try {
            init();
        } catch (Exception e) {
            Log.e(TAG, "Error in constructor", e);
        }
    }

    public Chessboardview(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            init();
        } catch (Exception e) {
            Log.e(TAG, "Error in constructor with attrs", e);
        }
    }

    public Chessboardview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        try {
            init();
        } catch (Exception e) {
            Log.e(TAG, "Error in constructor with attrs and defStyle", e);
        }
    }

    private void init() {
        Log.d(TAG, "Initializing ChessboardView");

        try {
            lightTilePaint = new Paint();
            lightTilePaint.setColor(Color.parseColor("#f0d9b5"));

            darkTilePaint = new Paint();
            darkTilePaint.setColor(Color.parseColor("#b58863"));

            highlightPaint = new Paint();
            highlightPaint.setColor(Color.parseColor("#a2e375"));
            highlightPaint.setAlpha(180);

            // Set a default tile size that will be updated in onSizeChanged
            tileSize = 100;

            Log.d(TAG, "Creating Chessboard");
            chessBoard = new Chessboard(this);

            Log.d(TAG, "Creating CheckScanner");
            checkScanner = new CheckScanner(chessBoard);

            Log.d(TAG, "Setting up board");
            setupBoard();

            Log.d(TAG, "ChessboardView initialization complete");
        } catch (Exception e) {
            Log.e(TAG, "Error during init", e);
        }
    }

    // Add getter method
    public int getTileSize() {
        return tileSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        try {
            super.onSizeChanged(w, h, oldw, oldh);
            Log.d(TAG, "onSizeChanged: w=" + w + ", h=" + h);

            tileSize = Math.min(w, h) / 8;
            if (tileSize <= 0) {
                // Fallback to a reasonable size if calculation fails
                tileSize = 100;
                Log.w(TAG, "Calculated tileSize was 0 or negative, using default: " + tileSize);
            }

            Log.d(TAG, "New tileSize: " + tileSize);

            if (chessBoard != null) {
                chessBoard.tileSize = tileSize;
            }

            // Load piece sprites with the correct size
            if (pieceList != null) {
                for (Piece piece : pieceList) {
                    if (piece != null) {
                        try {
                            piece.loadSprite(getContext(), tileSize);
                        } catch (Exception e) {
                            Log.e(TAG, "Error loading sprite for piece: " + piece.name, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onSizeChanged", e);
        }
    }

    // Rest of your methods with try-catch blocks around critical sections...

    public void setupBoard() {
        try {
            Log.d(TAG, "Setting up chess board");
            pieceList.clear();

            // Add pieces with error handling
            try {
                // Black pieces
                addPieceWithErrorHandling(new Rook(chessBoard, 0, 0, false));
                addPieceWithErrorHandling(new Knight(chessBoard, 1, 0, false));
                addPieceWithErrorHandling(new Bishop(chessBoard, 2, 0, false));
                addPieceWithErrorHandling(new Queen(chessBoard, 3, 0, false));
                addPieceWithErrorHandling(new King(chessBoard, 4, 0, false));
                addPieceWithErrorHandling(new Bishop(chessBoard, 5, 0, false));
                addPieceWithErrorHandling(new Knight(chessBoard, 6, 0, false));
                addPieceWithErrorHandling(new Rook(chessBoard, 7, 0, false));

                for (int col = 0; col < 8; col++) {
                    addPieceWithErrorHandling(new Pawn(chessBoard, col, 1, false));
                }

                // White pieces
                addPieceWithErrorHandling(new Rook(chessBoard, 0, 7, true));
                addPieceWithErrorHandling(new Knight(chessBoard, 1, 7, true));
                addPieceWithErrorHandling(new Bishop(chessBoard, 2, 7, true));
                addPieceWithErrorHandling(new Queen(chessBoard, 3, 7, true));
                addPieceWithErrorHandling(new King(chessBoard, 4, 7, true));
                addPieceWithErrorHandling(new Bishop(chessBoard, 5, 7, true));
                addPieceWithErrorHandling(new Knight(chessBoard, 6, 7, true));
                addPieceWithErrorHandling(new Rook(chessBoard, 7, 7, true));

                for (int col = 0; col < 8; col++) {
                    addPieceWithErrorHandling(new Pawn(chessBoard, col, 6, true));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error adding pieces", e);
            }

            // Initialize piece positions
            for (Piece piece : pieceList) {
                try {
                    piece.xPos = piece.col * tileSize;
                    piece.yPos = piece.row * tileSize;
                    piece.loadSprite(getContext(), tileSize);
                } catch (Exception e) {
                    Log.e(TAG, "Error initializing piece: " + piece.name, e);
                }
            }

            if (chessBoard != null) {
                chessBoard.pieceList = pieceList;
                Log.d(TAG, "Chess board setup complete with " + pieceList.size() + " pieces");
            } else {
                Log.e(TAG, "Chess board is null during setup");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in setupBoard", e);
        }
    }

    private void addPieceWithErrorHandling(Piece piece) {
        try {
            pieceList.add(piece);
        } catch (Exception e) {
            Log.e(TAG, "Error adding piece: " + piece.name, e);
        }
    }
}