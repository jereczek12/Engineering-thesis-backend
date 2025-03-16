package com.jereczek.checkers.game.model.board.constants;

import com.jereczek.checkers.enums.PieceTypes;

import java.util.HashMap;
import java.util.List;

import static com.jereczek.checkers.enums.PieceTypes.BLACK;
import static com.jereczek.checkers.enums.PieceTypes.WHITE;

public class BoardConstants {
    public static final int BOARD_DIMENSION = 10;

    public static final HashMap<Integer, PieceTypes> startingWhitePieces = initWhiteMap();

    public static final HashMap<Integer, PieceTypes> startingBlackPieces = initBlackMap();

    public static final int[][] startingBoardState = initStartingBoardState();

    public static final List<int[]> POSSIBLE_DIRECTIONS = List.of(
            new int[]{-1, -1},
            new int[]{-1, 1},
            new int[]{1, -1},
            new int[]{1, 1}
    );

    private static int[][] initStartingBoardState() {
        int[][] board = new int[BOARD_DIMENSION][BOARD_DIMENSION];
        for (int i = 0; i < BoardConstants.BOARD_DIMENSION; i++) {
            for (int j = 0; j < 4; j++) {
                if ((i + j) % 2 == 1) {
                    board[j][i] = BLACK.getValue();
                }
            }
        }
        for (int i = 0; i < BoardConstants.BOARD_DIMENSION; i++) {
            for (int j = 6; j < BoardConstants.BOARD_DIMENSION; j++) {
                if ((i + j) % 2 == 1) {
                    board[j][i] = WHITE.getValue();
                }
            }
        }
        return board;
    }

    private static HashMap<Integer, PieceTypes> initWhiteMap() {
        HashMap<Integer, PieceTypes> map = new HashMap<>();
        for (int i = 31; i <= 50; i++) {
            map.put(i, WHITE);
        }
        return map;
    }

    private static HashMap<Integer, PieceTypes> initBlackMap() {
        HashMap<Integer, PieceTypes> map = new HashMap<>();
        for (int i = 1; i <= 20; i++) {
            map.put(i, BLACK);
        }
        return map;
    }
}
