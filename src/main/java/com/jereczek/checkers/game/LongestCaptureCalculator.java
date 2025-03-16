package com.jereczek.checkers.game;

import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.game.model.board.BoardState;
import com.jereczek.checkers.game.model.board.BoardUtils;
import com.jereczek.checkers.game.model.board.Coordinates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jereczek.checkers.enums.PieceTypes.valueOfFigure;
import static com.jereczek.checkers.game.KingMoveValidator.checkIfCorrectLongKingCapture;
import static com.jereczek.checkers.game.model.board.BoardUtils.*;
import static com.jereczek.checkers.game.model.board.constants.BoardConstants.BOARD_DIMENSION;
import static com.jereczek.checkers.game.model.board.constants.BoardConstants.POSSIBLE_DIRECTIONS;

@Slf4j
@Component
public class LongestCaptureCalculator {
    public List<List<Move>> getLongestCapturesList(BoardState boardState) {
        List<List<Move>> longestSequences = new ArrayList<>();
        for (Map.Entry<Integer, PieceTypes> entry : boardState.getCurrentPlayerPieces().entrySet()) {
            Coordinates pieceCoords = Coordinates.fromSquareNumber(entry.getKey());
            List<List<Move>> availableCaptureSequences = findCaptureSequences(pieceCoords, boardState, entry.getValue(), new ArrayList<>());
            for (List<Move> sequence : availableCaptureSequences) {
                longestSequences = findLongestSequences(longestSequences, sequence);
            }
        }
        return longestSequences;
    }

    private List<List<Move>> findCaptureSequences(Coordinates startCoords, BoardState boardState,
                                                  PieceTypes pieceType, List<Coordinates> capturedPieces) {
        List<List<Move>> sequences = new ArrayList<>();

        if (pieceType.isRegularPiece()) {
            sequences.addAll(findRegularPieceCaptures(startCoords, boardState, pieceType, new ArrayList<>(), capturedPieces));
        } else if (pieceType.isKingPiece()) {
            sequences.addAll(findKingCaptures(startCoords, boardState, pieceType, new ArrayList<>(), capturedPieces));
        }

        return sequences.isEmpty() ? List.of(new ArrayList<>()) : sequences;
    }

    private List<List<Move>> findRegularPieceCaptures(Coordinates startCoords, BoardState boardState,
                                                      PieceTypes pieceType, List<Move> currentSequence, List<Coordinates> capturedPieces) {
        List<List<Move>> sequences = new ArrayList<>();

        for (int[] direction : POSSIBLE_DIRECTIONS) {
            Coordinates captureCoords = new Coordinates(startCoords.x() + direction[0],
                    startCoords.y() + direction[1]);
            Coordinates landingCoords = new Coordinates(captureCoords.x() + direction[0],
                    captureCoords.y() + direction[1]);

            if (!isValidCapture(boardState, captureCoords, landingCoords, pieceType, capturedPieces)) {
                continue;
            }

            BoardState newState = new BoardState(boardState);
            Move captureMove = new Move(startCoords.x(), startCoords.y(), landingCoords.x(), landingCoords.y());

            List<Coordinates> newCapturedPieces = new ArrayList<>(capturedPieces);
            newCapturedPieces.add(captureCoords);
            newState.markCaptured(captureCoords);
            newState.movePiece(captureMove, pieceType);

            List<Move> newSequence = new ArrayList<>(currentSequence);
            newSequence.add(captureMove);

            List<List<Move>> continuedCaptures = findCaptureSequences(landingCoords, newState,
                    pieceType, newCapturedPieces);

            if (continuedCaptures.isEmpty()) {
                sequences.add(newSequence);
            } else {
                for (List<Move> continuation : continuedCaptures) {
                    List<Move> completeSequence = new ArrayList<>(newSequence);
                    completeSequence.addAll(continuation);
                    sequences.add(completeSequence);
                }
            }
        }

        return sequences;
    }

    private List<List<Move>> findKingCaptures(Coordinates startCoords, BoardState boardState,
                                              PieceTypes pieceType, List<Move> currentSequence, List<Coordinates> capturedPieces) {
        List<List<Move>> sequences = new ArrayList<>();

        for (int[] direction : POSSIBLE_DIRECTIONS) {
            for (int distance = 1; distance < BOARD_DIMENSION; distance++) {
                Coordinates captureCoords = new Coordinates(startCoords.x() + direction[0] * distance,
                        startCoords.y() + direction[1] * distance);

                if (!isCoordinateWithinBoard(captureCoords) ||
                        capturedPieces.contains(captureCoords)) {
                    break;
                }

                Coordinates landingCoords = new Coordinates(captureCoords.x() + direction[0],
                        captureCoords.y() + direction[1]);

                if (!isValidKingCapture(boardState, startCoords, captureCoords, landingCoords, pieceType)) {
                    continue;
                }

                BoardState newState = new BoardState(boardState);
                Move captureMove = new Move(startCoords.x(), startCoords.y(), landingCoords.x(), landingCoords.y());

                List<Coordinates> newCapturedPieces = new ArrayList<>(capturedPieces);
                newCapturedPieces.add(captureCoords);
                newState.markCaptured(captureCoords);
                newState.movePiece(captureMove, pieceType);

                List<Move> newSequence = new ArrayList<>(currentSequence);
                newSequence.add(captureMove);

                List<List<Move>> continuedCaptures = findCaptureSequences(landingCoords, newState,
                        pieceType, newCapturedPieces);

                if (continuedCaptures.isEmpty()) {
                    sequences.add(newSequence);
                } else {
                    for (List<Move> continuation : continuedCaptures) {
                        List<Move> completeSequence = new ArrayList<>(newSequence);
                        completeSequence.addAll(continuation);
                        sequences.add(completeSequence);
                    }
                }
            }
        }

        return sequences;
    }

    private boolean isValidCapture(BoardState boardState, Coordinates captureCoords,
                                   Coordinates landingCoords, PieceTypes pieceType, List<Coordinates> capturedPieces) {
        if (!isCoordinateWithinBoard(captureCoords) ||
                !isCoordinateWithinBoard(landingCoords) ||
                capturedPieces.contains(captureCoords)) {
            return false;
        }

        PieceTypes capturePiece = PieceTypes.valueOfFigure(
                boardState.getBoardArray()[captureCoords.y()][captureCoords.x()]);

        return isMiddlePieceOpponentsAndLandingSquareEmpty(
                boardState.getBoardArray(), landingCoords, pieceType, capturePiece);
    }

    private boolean isValidKingCapture(BoardState boardState, Coordinates startCoords,
                                       Coordinates captureCoords, Coordinates landingCoords, PieceTypes pieceType) {
        if (!isValidCapturePosition(captureCoords, boardState.getBoardArray(), pieceType) ||
                !isSquareEmptyAndWithinBoard(landingCoords, boardState.getBoardArray())) {
            return false;
        }

        Move move = new Move(startCoords.x(), startCoords.y(), landingCoords.x(), landingCoords.y());
        return checkIfCorrectLongKingCapture(boardState.getBoardArray(), move);
    }

    private boolean isValidCapturePosition(Coordinates capturedPieceCoords, int[][] board, PieceTypes pieceType) {
        if (!BoardUtils.isCoordinateWithinBoard(capturedPieceCoords)) {
            return false;
        }
        PieceTypes pieceAtPosition = valueOfFigure(board[capturedPieceCoords.y()][capturedPieceCoords.x()]);
        return isOppositePiece(pieceType, pieceAtPosition);
    }

    public boolean isCaptureAvailable(BoardState boardState) {
        for (Map.Entry<Integer, PieceTypes> entry : boardState.getCurrentPlayerPieces().entrySet()) {
            Coordinates coords = Coordinates.fromSquareNumber(entry.getKey());
            PieceTypes pieceType = entry.getValue();

            if (pieceType == PieceTypes.WHITE || pieceType == PieceTypes.BLACK) {
                if (isRegularCaptureAvailable(boardState, coords)) {
                    return true;
                }
            } else {
                if (isKingCaptureAvailable(boardState.getBoardArray(), coords)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isKingCaptureAvailable(int[][] board, Coordinates coords) {
        for (int[] direction : POSSIBLE_DIRECTIONS) {
            int dx = direction[0];
            int dy = direction[1];
            for (int steps = 1; steps < BOARD_DIMENSION; steps++) {
                var checkCoords = new Coordinates(coords.x() + dx * steps, coords.y() + dy * steps);
                var landing = new Coordinates(checkCoords.x() + dx, checkCoords.y() + dy);
                if (!isCoordinateWithinBoard(landing)) {
                    break;
                }

                if (board[checkCoords.y()][checkCoords.x()] != PieceTypes.EMPTY.getValue()) {
                    if (checkIfCorrectLongKingCapture(board, new Move(coords.x(), coords.y(), landing.x(), landing.y()))) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    private boolean isRegularCaptureAvailable(BoardState boardState, Coordinates coords) {
        return POSSIBLE_DIRECTIONS.stream()
                .anyMatch(dir -> {
                    var captureCoords = new Coordinates(coords.x() + dir[0], coords.y() + dir[1]);
                    var landingCoords = new Coordinates(captureCoords.x() + dir[0], captureCoords.y() + dir[1]);
                    return isValidCapture(boardState, captureCoords, landingCoords);
                });
    }

    private boolean isValidCapture(BoardState boardState, Coordinates captureCoords, Coordinates landingCoords) {
        if (!isCoordinateWithinBoard(captureCoords) || !isCoordinateWithinBoard(landingCoords)) {
            return false;
        }

        PieceTypes movingPiece = boardState.getCurrentPlayer();
        PieceTypes capturePiece = PieceTypes.valueOfFigure(boardState.getBoardArray()[captureCoords.y()][captureCoords.x()]);

        return isMiddlePieceOpponentsAndLandingSquareEmpty(boardState.getBoardArray(), landingCoords, movingPiece, capturePiece);
    }

    private List<List<Move>> findLongestSequences(List<List<Move>> currentSequences, List<Move> newSequence) {
        if (currentSequences.isEmpty()) {
            return List.of(newSequence);
        }

        int currentLength = currentSequences.getFirst().size();
        int newLength = newSequence.size();

        if (newLength > currentLength) {
            return List.of(newSequence);
        } else if (newLength == currentLength) {
            List<List<Move>> result = new ArrayList<>(currentSequences);
            result.add(newSequence);
            return result;
        }
        return currentSequences;
    }
}

