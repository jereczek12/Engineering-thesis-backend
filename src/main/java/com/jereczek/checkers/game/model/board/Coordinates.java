package com.jereczek.checkers.game.model.board;

import static com.jereczek.checkers.game.model.board.constants.BoardConstants.BOARD_DIMENSION;

public record Coordinates(Integer x, Integer y) {

    public static Coordinates fromSquareNumber(Integer squareNumber) {
        int y = (squareNumber - 1) / (BOARD_DIMENSION / 2);
        int x = ((squareNumber - 1) % (BOARD_DIMENSION / 2)) * 2 + (y % 2 == 0 ? 1 : 0);

        return new Coordinates(x, y);
    }

    public static int coordinatesToSquareNumber(int x, int y) {
        if ((x + y) % 2 != 0) {
            return (y * BOARD_DIMENSION + x) / 2 + 1;
        } else {
            throw new RuntimeException("Coordinates not valid");
        }
    }
}
