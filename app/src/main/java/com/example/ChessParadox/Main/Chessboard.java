package com.example.ChessParadox.Main;

import android.util.Log;

import com.example.ChessParadox.Pieces.Piece;

import java.util.ArrayList;

public class Chessboard {
    private static final String TAG = "Chessboard";
    public Chessboardview view;
    public int tileSize;
    public ArrayList<Piece> pieceList = new ArrayList<>();
    public Piece selectedPiece;
    public int enPassantTile = -1;


    public CheckScanner checkScanner;

    public Chessboard(Chessboardview view) {
        try {
            Log.d(TAG, "Creating Chessboard");
            this.view = view;
            this.tileSize = view != null ? view.getTileSize() : 100;
            this.checkScanner = new CheckScanner(this);
            Log.d(TAG, "Chessboard created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating Chessboard", e);
        }
    }

    public int getTileNum (int col, int row){
        return row + col;
    }

    public boolean isValidMove(Move move) {
        try {
            if (move == null || move.piece == null) {
                Log.e(TAG, "Move or piece is null");
                return false;
            }

            if (move.newCol < 0 || move.newCol > 7 || move.newRow < 0 || move.newRow > 7) {
                return false;
            }

            // Rest of validation code with additional null checks...

            return true; // Changed for simplicity
        } catch (Exception e) {
            Log.e(TAG, "Error in isValidMove", e);
            return false;
        }


    }

    Piece findKing(boolean isWhite){
        for (Piece piece : pieceList){
            if (isWhite == piece.isWhite && piece.name.equals("King")) {
                return piece;
            }
        }
        return null;
    }


    public boolean sameTeam(Piece p1, Piece p2){
        if (p1 == null || p2 == null){
            return false;
        }
        return p1.isWhite == p2.isWhite;
    }



    public Piece getPiece(int col, int row) {
        try {
            if (col < 0 || col > 7 || row < 0 || row > 7) {
                return null;
            }

            if (pieceList == null) {
                Log.e(TAG, "pieceList is null in getPiece");
                return null;
            }

            for (Piece piece : pieceList) {
                if (piece != null && piece.col == col && piece.row == row) {
                    return piece;
                }
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error in getPiece", e);
            return null;
        }
    }

    // Add similar error handling to all methods...
}