package com.jereczek.checkers.game.model.board;

import com.jereczek.checkers.enums.PieceTypes;

import static com.jereczek.checkers.enums.PieceTypes.*;
import static com.jereczek.checkers.game.model.board.constants.BoardConstants.BOARD_DIMENSION;

public class BoardUtils {
    public static boolean isCoordinateWithinBoard(Coordinates coordinates) {
        return coordinates.x() >= 0 && coordinates.x() < BOARD_DIMENSION && coordinates.y() >= 0 && coordinates.y() < BOARD_DIMENSION;
    }

    public static boolean isSquareEmptyAndWithinBoard(Coordinates coordinates, int[][] boardArray) {
        return isCoordinateWithinBoard(coordinates) && isSquareEmpty(coordinates, boardArray);
    }

    public static boolean isSquareEmpty(Coordinates coordinates, int[][] boardArray) {
        return boardArray[coordinates.y()][coordinates.x()] == PieceTypes.EMPTY.getValue();
    }

    public static boolean isMiddlePieceOpponentsAndLandingSquareEmpty(int[][] boardArray, Coordinates landingCoords, PieceTypes movingPiece, PieceTypes capturePiece) {
        return isOppositePiece(movingPiece, capturePiece)
                && BoardUtils.isSquareEmpty(landingCoords, boardArray);
    }

    public static boolean isOppositePiece(PieceTypes allyPiece, PieceTypes checkedPiece) {
        return switch (allyPiece) {
            case WHITE, WHITE_KING -> checkedPiece == BLACK || checkedPiece == BLACK_KING;
            case BLACK, BLACK_KING -> checkedPiece == WHITE || checkedPiece == WHITE_KING;
            default -> false;
        };
    }
}
