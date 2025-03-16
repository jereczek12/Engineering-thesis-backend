package com.jereczek.checkers.game;

import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.game.model.board.BoardState;
import com.jereczek.checkers.game.model.board.Coordinates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static com.jereczek.checkers.enums.PieceTypes.*;
import static com.jereczek.checkers.game.model.board.BoardUtils.isOppositePiece;
import static com.jereczek.checkers.game.model.board.Coordinates.coordinatesToSquareNumber;

@Slf4j
@Service
public class CheckersGameLogic {
    private final MoveValidator moveValidator;

    public CheckersGameLogic(MoveValidator moveValidator) {
        this.moveValidator = moveValidator;
    }

    public BoardState performMoves(List<Move> moves, BoardState boardState, boolean validate) {
        var capturedPieces = new LinkedList<Coordinates>();
        if (validate) {
            moveValidator.checkIfLongestCapture(moves, boardState);
        }
        PieceTypes movingPieceType = valueOfFigure(boardState.getBoardArray()[moves.getFirst().startY()][moves.getFirst().startX()]);
        for (Move move : moves) {
            var isCapture = checkIfMoveIsCapture(boardState, move, movingPieceType);

            moveValidator.isMoveLegal(boardState, move, movingPieceType, isCapture).ifPresent(exc -> {
                throw exc;
            });

            if (isCapture) {
                capturePiece(move, boardState, capturedPieces);
            }
            boardState.movePiece(move, movingPieceType);
        }

        boardState.cleanupCaptured(capturedPieces);

        promotePiece(boardState, moves.getLast(), movingPieceType);

        boardState.setCurrentPlayer(GameStateUtils.getNextPlayerType(boardState.getCurrentPlayer()));
        return boardState;
    }

    private void capturePiece(Move move, BoardState boardState, List<Coordinates> capturedPieces) {
        int distanceX = Math.abs(move.endX() - move.startX());
        int stepX = (move.endX() - move.startX()) / distanceX;
        int stepY = (move.endY() - move.startY()) / distanceX;
        for (int i = 1; i < distanceX; i++) {
            var captured = new Coordinates(move.startX() + i * stepX, move.startY() + i * stepY);
            if (boardState.getBoardArray()[captured.y()][captured.x()] != PieceTypes.EMPTY.getValue()) {
                boardState.markCaptured(captured);
                capturedPieces.add(captured);
            }
        }
    }

    private void promotePiece(BoardState boardState, Move move, PieceTypes pieceType) {
        if ((pieceType == PieceTypes.WHITE && move.endY() == 0)
                || (pieceType == PieceTypes.BLACK && move.endY() == boardState.getBoardArray().length - 1)) {
            pieceType = (pieceType == PieceTypes.WHITE) ? PieceTypes.WHITE_KING : PieceTypes.BLACK_KING;
            boardState.getBoardArray()[move.endY()][move.endX()] = pieceType.getValue();

            if (pieceType == PieceTypes.BLACK_KING) {
                boardState.getBlackPieces().replace(coordinatesToSquareNumber(move.endX(), move.endY()), pieceType);
            } else {
                boardState.getWhitePieces().replace(coordinatesToSquareNumber(move.endX(), move.endY()), pieceType);
            }
        }
    }

    private boolean checkIfMoveIsCapture(BoardState boardState, Move move, PieceTypes movingPieceType) {
        int distanceX = Math.abs(move.endX() - move.startX());
        if (distanceX == 2 && movingPieceType.isRegularPiece()) {
            PieceTypes middlePiece = valueOfFigure(boardState.getBoardArray()[(move.startY() + move.endY()) / 2][(move.startX() + move.endX()) / 2]);
            return isOppositePiece(boardState.getCurrentPlayer(), middlePiece);
        } else if (movingPieceType.isKingPiece()) {
            return KingMoveValidator.checkIfCorrectLongKingCapture(boardState.getBoardArray(), move);
        }
        return false;
    }
}
