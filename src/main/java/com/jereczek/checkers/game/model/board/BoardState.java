package com.jereczek.checkers.game.model.board;

import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.game.Move;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jereczek.checkers.enums.PieceTypes.*;
import static com.jereczek.checkers.game.model.board.Coordinates.coordinatesToSquareNumber;
import static com.jereczek.checkers.game.model.board.constants.BoardConstants.*;

@Data
@Builder
@AllArgsConstructor
public class BoardState {
    private Map<Integer, PieceTypes> whitePieces;

    private Map<Integer, PieceTypes> blackPieces;

    private int[][] boardArray;

    private PieceTypes currentPlayer;

    public BoardState() {
        this.whitePieces = startingWhitePieces;
        this.blackPieces = startingBlackPieces;
        this.boardArray = startingBoardState;
        this.currentPlayer = PieceTypes.WHITE;
    }

    public BoardState(BoardState other) {
        this.whitePieces = new HashMap<>(other.whitePieces);
        this.blackPieces = new HashMap<>(other.blackPieces);
        this.boardArray = Arrays.stream(other.boardArray).map(int[]::clone).toArray(int[][]::new);
        this.currentPlayer = other.currentPlayer;
    }

    public void movePiece(Move move, PieceTypes movingPieceType) {
        boardArray[move.startY()][move.startX()] = 0;
        boardArray[move.endY()][move.endX()] = movingPieceType.getValue();

        if (movingPieceType.equals(BLACK) || movingPieceType.equals(BLACK_KING)) {
            this.getBlackPieces().remove(coordinatesToSquareNumber(move.startX(), move.startY()));
            this.getBlackPieces().put(coordinatesToSquareNumber(move.endX(), move.endY()), movingPieceType);
        }
        if (movingPieceType.equals(WHITE) || movingPieceType.equals(WHITE_KING)) {
            this.getWhitePieces().remove(coordinatesToSquareNumber(move.startX(), move.startY()));
            this.getWhitePieces().put(coordinatesToSquareNumber(move.endX(), move.endY()), movingPieceType);
        }
    }

    public void cleanupCaptured(List<Coordinates> capturedPieces) {
        whitePieces.values().removeIf(PieceTypes.CAPTURED::equals);
        blackPieces.values().removeIf(PieceTypes.CAPTURED::equals);
        for (Coordinates capturedPiece : capturedPieces) {
            boardArray[capturedPiece.y()][capturedPiece.x()] = EMPTY.getValue();
        }
    }

    public Map<Integer, PieceTypes> getCurrentPlayerPieces() {
        return getPiecesForPlayer(currentPlayer);
    }

    public Map<Integer, PieceTypes> getPiecesForPlayer(PieceTypes player) {
        return (player == WHITE || player == WHITE_KING) ?
                this.getWhitePieces() : this.getBlackPieces();
    }

    public void markCaptured(Coordinates coordinates) {
        int squareNumber = coordinatesToSquareNumber(coordinates.x(), coordinates.y());
        getWhitePieces().replace(squareNumber, CAPTURED);
        getBlackPieces().replace(squareNumber, CAPTURED);
    }
}
