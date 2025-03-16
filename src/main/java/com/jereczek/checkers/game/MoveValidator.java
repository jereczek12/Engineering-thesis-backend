package com.jereczek.checkers.game;

import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.exception.IllegalMoveException;
import com.jereczek.checkers.game.model.board.BoardState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.jereczek.checkers.game.model.board.Coordinates.coordinatesToSquareNumber;

@Slf4j
@Service
public class MoveValidator {
    private final LongestCaptureCalculator longestCaptureCalculator;

    public MoveValidator(LongestCaptureCalculator longestCaptureCalculator) {
        this.longestCaptureCalculator = longestCaptureCalculator;
    }

    public Optional<RuntimeException> isMoveLegal(BoardState board, Move move, PieceTypes piece, boolean isCapture) {
        int distanceX = Math.abs(move.endX() - move.startX());
        int distanceY = Math.abs(move.endY() - move.startY());

        if (piece == PieceTypes.EMPTY) {
            return Optional.of(new IllegalMoveException("No Piece on starting position!"));
        }

        if (board.getBoardArray()[move.endY()][move.endX()] != PieceTypes.EMPTY.getValue()) {
            return Optional.of(new IllegalMoveException("Ending Position not empty!"));
        }

        if (distanceX != distanceY) {
            return Optional.of(new IllegalMoveException("Move is not diagonal!"));
        }
        if ((piece == PieceTypes.WHITE && move.endY() > move.startY())
                || (piece == PieceTypes.BLACK && move.endY() < move.startY())) {
            if (distanceX == 1)
                return Optional.of(new IllegalMoveException("Piece " +
                        coordinatesToSquareNumber(move.startX(), move.startY()) + " tried to move backwards!"));
        }

        if (distanceX > 2 && (piece != PieceTypes.WHITE_KING && piece != PieceTypes.BLACK_KING)) {
            return Optional.of(new IllegalMoveException("Move was too long for a pawn!!"));
        }
        if (distanceX == 2 && (piece != PieceTypes.WHITE_KING && piece != PieceTypes.BLACK_KING)) {
            if (!isCapture)
                return Optional.of(new IllegalMoveException("Move was too long for a pawn!"));
        }

        if ((piece == PieceTypes.WHITE_KING || piece == PieceTypes.BLACK_KING) && distanceX > 1) {
            if (!isCapture)
                if (!KingMoveValidator.checkIfCorrectLongKingMove(board.getBoardArray(), move, distanceX)) {
                    return Optional.of(new IllegalMoveException("King tried to move over an ally piece or multiple enemy pieces"));
                }
        }
        return Optional.empty();
    }

    public void checkIfLongestCapture(List<Move> moves, BoardState boardState) {
        if (!longestCaptureCalculator.isCaptureAvailable(boardState)) {
            return;
        }
        List<List<Move>> longestSequenceAvailable = longestCaptureCalculator.getLongestCapturesList(boardState);
        if (!longestSequenceAvailable.getFirst().isEmpty()) {
            if (!longestSequenceAvailable.contains(moves)) {
                throw new IllegalMoveException(String.format("Longer capture sequence (%d) available than move %d-%d",
                        longestSequenceAvailable.getFirst().size(),
                        moves.getFirst().startAsSquare(), moves.getLast().endAsSquare()));
            }
        }
    }
}
