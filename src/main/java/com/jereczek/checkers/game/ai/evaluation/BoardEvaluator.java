package com.jereczek.checkers.game.ai.evaluation;

import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.game.model.board.BoardState;
import com.jereczek.checkers.game.model.board.Coordinates;

import java.util.Map;
import java.util.Set;

import static com.jereczek.checkers.enums.PieceTypes.*;
import static com.jereczek.checkers.game.ai.evaluation.EvaluationConstants.*;
import static com.jereczek.checkers.game.ai.evaluation.PositionsConstants.*;
import static com.jereczek.checkers.game.model.board.constants.BoardConstants.BOARD_DIMENSION;

public class BoardEvaluator {
    private final BoardState boardState;

    public BoardEvaluator(BoardState boardState) {
        this.boardState = boardState;
    }

    public Integer evaluateBoard() {
        boolean isEndgame = isEndgame(boardState);

        int whiteScore = evaluatePhase(boardState.getWhitePieces(), isEndgame) +
                evaluateFormation(boardState.getWhitePieces());

        int blackScore = evaluatePhase(boardState.getBlackPieces(), isEndgame) +
                evaluateFormation(boardState.getBlackPieces());

        return whiteScore - blackScore;
    }

    private int pieceValue(PieceTypes type, Integer position, boolean isEndgame) {
        int value;

        if (type.isRegularPiece()) {
            value = PIECE_VALUE;
            Coordinates coords = Coordinates.fromSquareNumber(position);
            int advancement = (type == WHITE) ?
                    (BOARD_DIMENSION - 1 - coords.y()) :
                    coords.y();
            value += advancement * ADVANCEMENT_BONUS;
        } else {
            value = evaluateKing(position, isEndgame);
        }

        if (!isEndgame) {
            if (type.isRegularPiece() && isBackRow(type, position)) {
                value += BACK_ROW_PIECE_BONUS;
            }


            if (SAFE_COLUMNS.contains(position)) {
                value += SAFE_COLUMN_BONUS;
            }

            if (isControllingMiddle(position)) {
                value *= CENTER_MODIFIER;
            }
        }

        if (isEndgame) {
            if (isControllingMiddle(position)) {
                value += ENDGAME_CENTER_BONUS;
            }

            if (type.isRegularPiece()) {
                Coordinates coords = Coordinates.fromSquareNumber(position);
                int distanceToPromotion = (type == WHITE) ? coords.y() : (BOARD_DIMENSION - 1 - coords.y());
                if (distanceToPromotion <= 2) {
                    value += (3 - distanceToPromotion) * 30;
                }
            }

            if (type == WHITE_KING || type == BLACK_KING) {
                value *= ENDGAME_KING_MODIFIER;
            }
        }

        if (isInDanger(position, type)) {
            value *= DANGER_MODIFIER;
        }

        return value;
    }

    private boolean isControllingMiddle(Integer position) {
        return CENTER_SQUARES.contains(position);
    }

    private boolean isBackRow(PieceTypes pieceTypes, Integer position) {
        if (pieceTypes.isRegularPiece()) {
            return (pieceTypes == WHITE) ? WHITE_BACK_ROW.contains(position) : BLACK_BACK_ROW.contains(position);
        }
        return false;
    }

    private boolean isInDanger(Integer position, PieceTypes piece) {
        if (SAFE_COLUMNS.contains(position)) {
            return false;
        }
        Map<Integer, PieceTypes> opponentPieces;

        opponentPieces = (piece == WHITE || piece == WHITE_KING) ? boardState.getBlackPieces() : boardState.getWhitePieces();

        int[] directions = getAdjacentDirections(position);

        for (int dir : directions) {
            Integer diagonalAdjacent = position + dir;
            if (isOpponentPiece(diagonalAdjacent, opponentPieces) && isEmptySquare(position + 2 * dir)) {
                return true;
            }
        }
        return false;
    }

    private int[] getAdjacentDirections(Integer position) {
        boolean isEvenRow = ((position - 1) / 5) % 2 == 0;
        if (isEvenRow) {
            return new int[]{-4, -5, 5, 6};
        } else {
            return new int[]{-6, -5, 4, 5};
        }
    }

    private boolean isOpponentPiece(Integer position, Map<Integer, PieceTypes> opponentPieces) {
        return opponentPieces.containsKey(position);
    }

    private boolean isEmptySquare(Integer position) {
        return !boardState.getWhitePieces().containsKey(position) && boardState.getBlackPieces().containsKey(position);
    }

    private boolean isEndgame(BoardState state) {
        int totalPieces = state.getWhitePieces().size() + state.getBlackPieces().size();
        return totalPieces <= 8;
    }

    private int evaluatePhase(Map<Integer, PieceTypes> piecesMap, boolean isEndgame) {
        int value = 0;
        for (Map.Entry<Integer, PieceTypes> piece : piecesMap.entrySet()) {
            value += pieceValue(piece.getValue(), piece.getKey(), isEndgame);
        }
        return value;
    }

    private int evaluateFormation(Map<Integer, PieceTypes> pieces) {
        int formationValue = 0;
        Set<Integer> piecesPositions = pieces.keySet();

        for (Integer pos : piecesPositions) {
            if (piecesPositions.contains(pos + 5) ||
                    piecesPositions.contains(pos + 6) ||
                    piecesPositions.contains(pos - 5) ||
                    piecesPositions.contains(pos - 6)) {
                formationValue += FORMATION_BONUS;
            }
        }
        return formationValue;
    }

    private int evaluateKing(int position, boolean isEndgame) {
        int value = KING_VALUE;

        if (isEndgame) {
            value += evaluateKingPosition(position);
            if (isControllingMiddle(position)) {
                value *= ENDGAME_KING_CENTER_MODIFIER;
            }
        }

        return value;
    }

    private int evaluateKingPosition(int position) {
        Coordinates coords = Coordinates.fromSquareNumber(position);
        int centerDistance = Math.abs(coords.x() - 5) + Math.abs(coords.y() - 5);
        return (10 - centerDistance) * 10;
    }
}
