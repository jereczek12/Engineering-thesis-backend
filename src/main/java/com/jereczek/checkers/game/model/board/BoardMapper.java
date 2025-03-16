package com.jereczek.checkers.game.model.board;

import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.model.BoardEntity;
import com.jereczek.checkers.piece.PiecesNotationMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jereczek.checkers.game.model.board.constants.BoardConstants.BOARD_DIMENSION;

public class BoardMapper {
    public static BoardState entityToBoardStateMapper(BoardEntity entity) {
        BoardState boardState = BoardState.builder()
                .boardArray(new int[BOARD_DIMENSION][BOARD_DIMENSION])
                .blackPieces(new HashMap<>())
                .whitePieces(new HashMap<>())
                .currentPlayer(entity.getCurrentPlayer())
                .build();
        populateBoardStateFromEntity(boardState, entity.getBlackPieces(), PieceTypes.BLACK);
        populateBoardStateFromEntity(boardState, entity.getWhitePieces(), PieceTypes.WHITE);
        populateBoardStateFromEntity(boardState, entity.getBlackKings(), PieceTypes.BLACK_KING);
        populateBoardStateFromEntity(boardState, entity.getWhiteKings(), PieceTypes.WHITE_KING);

        return boardState;
    }

    public static BoardEntity updateEntityFromBoardState(BoardEntity entity, BoardState boardState) {
        String[] whites = PiecesNotationMapper.convertPiecesToString(boardState.getWhitePieces());
        entity.setWhitePieces(whites[0]);
        entity.setWhiteKings(whites[1]);
        String[] blacks = PiecesNotationMapper.convertPiecesToString(boardState.getBlackPieces());
        entity.setBlackPieces(blacks[0]);
        entity.setBlackKings(blacks[1]);
        entity.setCurrentPlayer(boardState.getCurrentPlayer());

        entity.setBoard(map2dArrayToString(boardState.getBoardArray()));
        return entity;
    }

    private static void populateBoardStateFromEntity(BoardState boardState, String piecesList, PieceTypes type) {
        if (piecesList.isEmpty()) {
            return;
        }
        Map<Integer, PieceTypes> map;
        switch (type) {
            case WHITE, WHITE_KING -> map = boardState.getWhitePieces();
            case BLACK, BLACK_KING -> map = boardState.getBlackPieces();
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
        int[][] boardArray = boardState.getBoardArray();
        String[] pieceNotations = piecesList.split(";");
        for (String pieceNotation : pieceNotations) {
            String[] parts = pieceNotation.split("-");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            boardArray[y][x] = type.getValue();
            map.put(Coordinates.coordinatesToSquareNumber(x, y), type);
        }
        boardState.setBoardArray(boardArray);
        switch (type) {
            case WHITE, WHITE_KING -> boardState.setWhitePieces(map);
            case BLACK, BLACK_KING -> boardState.setBlackPieces(map);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public static String map2dArrayToString(int[][] boardArray) {
        return "[" + Arrays.stream(boardArray)
                .map(row -> "[" + Arrays.stream(row)
                        .mapToObj(Integer::toString)
                        .collect(Collectors.joining(",")) + "]")
                .collect(Collectors.joining(",")) + "]";
    }

}
