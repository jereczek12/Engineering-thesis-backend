package com.jereczek.checkers.game;

import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.game.model.board.Coordinates;

import static com.jereczek.checkers.enums.PieceTypes.EMPTY;
import static com.jereczek.checkers.game.model.board.BoardUtils.isOppositePiece;
import static com.jereczek.checkers.game.model.board.BoardUtils.isSquareEmptyAndWithinBoard;

public class KingMoveValidator {
    public static boolean checkIfCorrectLongKingMove(int[][] board, Move move, int distanceX) {
        int stepX = (move.endX() - move.startX()) / distanceX;
        int stepY = (move.endY() - move.startY()) / distanceX;
        for (int i = 1; i < distanceX; i++) {
            int middleX = move.startX() + i * stepX;
            int middleY = move.startY() + i * stepY;
            if (board[middleY][middleX] != PieceTypes.EMPTY.getValue()) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkIfCorrectLongKingCapture(int[][] board, Move move) {
        if (!isSquareEmptyAndWithinBoard(new Coordinates(move.endX(), move.endY()), board)) {
            return false;
        }

        int distanceX = Math.abs(move.endX() - move.startX());
        int distanceY = Math.abs(move.endY() - move.startY());
        int stepX = (move.endX() - move.startX()) / distanceX;
        int stepY = (move.endY() - move.startY()) / distanceY;
        int piecesInBetween = 0;
        for (int i = 1; i < distanceX; i++) {
            int middleX = move.startX() + i * stepX;
            int middleY = move.startY() + i * stepY;
            PieceTypes middlePiece = PieceTypes.valueOfFigure(board[middleY][middleX]);
            PieceTypes movingPiece = PieceTypes.valueOfFigure(board[move.startY()][move.startX()]);
            if (isOppositePiece(movingPiece, middlePiece)) {
                piecesInBetween++;
            }
            if (piecesInBetween > 1) return false;
            if (!isOppositePiece(movingPiece, middlePiece) && middlePiece != EMPTY) return false;
        }
        return piecesInBetween == 1;
    }
}
