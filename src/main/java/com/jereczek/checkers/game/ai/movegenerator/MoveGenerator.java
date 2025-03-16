package com.jereczek.checkers.game.ai.movegenerator;

import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.game.LongestCaptureCalculator;
import com.jereczek.checkers.game.Move;
import com.jereczek.checkers.game.MoveValidator;
import com.jereczek.checkers.game.model.board.BoardState;
import com.jereczek.checkers.game.model.board.Coordinates;
import com.jereczek.checkers.game.model.board.constants.BoardConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jereczek.checkers.game.KingMoveValidator.checkIfCorrectLongKingMove;
import static com.jereczek.checkers.game.model.board.BoardUtils.isSquareEmptyAndWithinBoard;
import static com.jereczek.checkers.game.model.board.constants.BoardConstants.BOARD_DIMENSION;

@Slf4j
@Service
public class MoveGenerator {
    private final LongestCaptureCalculator longestCaptureCalculator;

    private final MoveValidator moveValidator;

    public MoveGenerator(MoveValidator moveValidator, LongestCaptureCalculator longestCaptureCalculator) {
        this.moveValidator = moveValidator;
        this.longestCaptureCalculator = longestCaptureCalculator;
    }

    public List<List<Move>> generateMoves(BoardState boardState) {
        List<List<Move>> validMoves = new ArrayList<>();
        if (longestCaptureCalculator.isCaptureAvailable(boardState)) {
            return longestCaptureCalculator.getLongestCapturesList(boardState);
        }

        for (Map.Entry<Integer, PieceTypes> piece : boardState.getCurrentPlayerPieces().entrySet()) {
            int piecePosition = piece.getKey();
            PieceTypes pieceType = piece.getValue();
            List<Move> potentialMoves = generatePotentialMovesForPiece(piecePosition, pieceType, boardState);

            for (Move move : potentialMoves) {
                if (moveValidator.isMoveLegal(boardState, move, pieceType, false).isEmpty()) {
                    validMoves.add(List.of(move));
                }
            }
        }
        return validMoves;
    }

    private List<Move> generatePotentialMovesForPiece(int piecePosition, PieceTypes pieceType, BoardState boardState) {
        List<Move> potentialMoves = new ArrayList<>();
        Coordinates coords = Coordinates.fromSquareNumber(piecePosition);
        if (pieceType.isRegularPiece()) {
            potentialMoves.addAll(generateRegularMoves(coords, boardState));
        } else if (pieceType.isKingPiece()) {
            potentialMoves.addAll(generateKingMoves(coords, boardState));
        }
        return potentialMoves;
    }

    private List<Move> generateKingMoves(Coordinates coords, BoardState boardState) {
        List<Move> kingMoves = new ArrayList<>();
        for (int[] direction : BoardConstants.POSSIBLE_DIRECTIONS) {
            for (int distance = 1; distance <= BOARD_DIMENSION; distance++) {
                var newCoords = new Coordinates(coords.x() + direction[0] * distance,
                        coords.y() + direction[1] * distance);

                if (isSquareEmptyAndWithinBoard(newCoords, boardState.getBoardArray())) {
                    Move move = new Move(coords.x(), coords.y(), newCoords.x(), newCoords.y());
                    if (checkIfCorrectLongKingMove(boardState.getBoardArray(), move, distance)) {
                        kingMoves.add(move);
                    }
                } else {
                    break;
                }
            }
        }
        return kingMoves;
    }

    private List<Move> generateRegularMoves(Coordinates coords, BoardState boardState) {
        List<Move> regularMoves = new ArrayList<>();
        for (int[] direction : BoardConstants.POSSIBLE_DIRECTIONS) {
            var newCoords = new Coordinates(coords.x() + direction[0], coords.y() + direction[1]);

            if (isSquareEmptyAndWithinBoard(newCoords, boardState.getBoardArray())) {
                regularMoves.add(new Move(coords.x(), coords.y(), newCoords.x(), newCoords.y()));
            }
        }
        return regularMoves;
    }
}
