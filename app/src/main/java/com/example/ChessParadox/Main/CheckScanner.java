package com.example.ChessParadox.Main;

import com.example.ChessParadox.Pieces.Piece;



public class CheckScanner {

    Chessboard chessBoard;

    public CheckScanner(Chessboard chessBoard) {
        this.chessBoard = chessBoard;

    }

    public boolean isKingInCheck(Move move) {

        Piece king = chessBoard.findKing(move.piece.isWhite);
        if (king == null) return false;

        int kingCol = king.col;
        int kingRow = king.row;

        if (chessBoard.selectedPiece != null && chessBoard.selectedPiece.name.equals("King")) {
            kingCol = move.newCol;
            kingRow = move.newRow;
        }

        return hitByRook(move.newCol, move.newRow, king, kingCol, kingRow, 0, 1) || // up
                hitByRook(move.newCol, move.newRow, king, kingCol, kingRow, 1, 0) || // right
                hitByRook(move.newCol, move.newRow, king, kingCol, kingRow, 0, -1) || // down
                hitByRook(move.newCol, move.newRow, king, kingCol, kingRow, -1, 0) || // left

                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, -1, -1) || // up left
                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, 1, -1) || // up right
                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, 1, 1) || // down right
                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, -1, 1) || // down left

                hitByKnight(move.newCol, move.newRow, king, kingCol, kingRow) ||
                hitByPawn(move.newCol, move.newRow, king, kingCol, kingRow) ||
                hitByKing(king, kingCol, kingRow);
    }

    private boolean hitByRook(int col, int row, Piece king, int kingCol, int kingRow, int colVal, int rowVal) {
        for (int i = 1; i < 8; i++) {
            if (kingCol + (i * colVal) == col && kingRow + (i * rowVal) == row) {
                break;
            }

            Piece piece = chessBoard.getPiece(kingCol + (i * colVal), kingRow + (i * rowVal));
            if (piece != null && piece != chessBoard.selectedPiece) {
                if (!chessBoard.sameTeam(piece, king) && (piece.name.equals("Rook") || piece.name.equals("Queen"))) {
                    return true;
                }
                break;
            }
        }
        return false;
    }



    private boolean hitByBishop(int col, int row, Piece king, int kingCol, int kingRow, int colVal, int rowVal) {
        for (int i = 1; i < 8; i++) {
            if (kingCol - (i * colVal) == col && kingRow - (i * rowVal) == row) {
                break;
            }

            Piece piece = chessBoard.getPiece((kingCol - (i * colVal)), kingRow - (i * rowVal));
            if (piece != null && piece != chessBoard.selectedPiece) {
                if (!chessBoard.sameTeam(piece, king) && (piece.name.equals("Bishop") || piece.name.equals("Queen"))) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private boolean hitByKnight(int col, int row, Piece king, int kingCol, int kingRow) {
        return checkKnight(chessBoard.getPiece(kingCol - 1, kingRow - 2), king, col, row) ||
                checkKnight(chessBoard.getPiece(kingCol + 1, kingRow - 2), king, col, row) ||
                checkKnight(chessBoard.getPiece(kingCol + 2, kingRow - 1), king, col, row) ||
                checkKnight(chessBoard.getPiece(kingCol + 2, kingRow + 1), king, col, row) ||
                checkKnight(chessBoard.getPiece(kingCol + 1, kingRow + 2), king, col, row) ||
                checkKnight(chessBoard.getPiece(kingCol - 1, kingRow + 2), king, col, row) ||
                checkKnight(chessBoard.getPiece(kingCol - 2, kingRow + 1), king, col, row) ||
                checkKnight(chessBoard.getPiece(kingCol - 2, kingRow - 1), king, col, row);
    }

    private boolean checkKnight(Piece p, Piece k, int col, int row) {
        return p != null && !chessBoard.sameTeam(p, k) && p.name.equals("Knight") && !(p.col == col && p.row == row);
    }

    private boolean hitByKing(Piece king, int kingCol, int kingRow) {
        return checkKing(chessBoard.getPiece(kingCol - 1, kingRow - 1), king) ||
                checkKing(chessBoard.getPiece(kingCol + 1, kingRow - 1), king) ||
                checkKing(chessBoard.getPiece(kingCol, kingRow - 1), king) ||
                checkKing(chessBoard.getPiece(kingCol - 1, kingRow), king) ||
                checkKing(chessBoard.getPiece(kingCol + 1, kingRow), king) ||
                checkKing(chessBoard.getPiece(kingCol - 1, kingRow + 1), king) ||
                checkKing(chessBoard.getPiece(kingCol + 1, kingRow + 1), king) ||
                checkKing(chessBoard.getPiece(kingCol, kingRow + 1), king);
    }

    private boolean checkKing(Piece p, Piece k) {
        return p != null && !chessBoard.sameTeam(p, k) && p.name.equals("King");
    }

    private boolean hitByPawn(int col, int row, Piece king, int kingCol, int kingRow) {
        int colorVal = king.isWhite ? -1 : +1;
        return checkPawn(chessBoard.getPiece(kingCol + 1, kingRow + colorVal), king, col, row) ||
                checkPawn(chessBoard.getPiece(kingCol - 1, kingRow + colorVal), king, col, row);
    }

    private boolean checkPawn(Piece p, Piece k, int col, int row) {
        return p != null && !chessBoard.sameTeam(p, k) && p.name.equals("Pawn") && !(p.col == col && p.row == row);
    }

    public boolean isGameOver(Piece king) {
        for (Piece piece : chessBoard.pieceList) {
            if (chessBoard.sameTeam(piece, king)) {
                chessBoard.selectedPiece = piece == king ? king : null;
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        Move move = new Move(chessBoard, piece, col, row);
                        if (chessBoard.isValidMove(move)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}