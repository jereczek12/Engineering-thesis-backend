package com.jereczek.checkers.movehelper;

import com.jereczek.checkers.controller.dto.MoveDTO;
import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.game.CheckWinnerService;
import com.jereczek.checkers.game.CheckersGameLogic;
import com.jereczek.checkers.game.Move;
import com.jereczek.checkers.game.ai.MiniMax;
import com.jereczek.checkers.game.ai.cache.BoardStateCacheService;
import com.jereczek.checkers.game.model.board.BoardState;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipGenerator {
    public static final int TIP_DEPTH_HANDICAP = 2;

    private final CheckersGameLogic checkersGameLogic;

    private final CheckWinnerService checkWinnerService;

    private final MiniMax miniMax;

    private final BoardStateCacheService boardStateCacheService;

    public TipGenerator(CheckersGameLogic checkersGameLogic, CheckWinnerService checkWinnerService, MiniMax miniMax, BoardStateCacheService boardStateCacheService) {
        this.checkersGameLogic = checkersGameLogic;
        this.checkWinnerService = checkWinnerService;
        this.miniMax = miniMax;
        this.boardStateCacheService = boardStateCacheService;
    }

    public TipModel generateTips(int difficulty, BoardState boardState, PieceTypes player) {
        TipModel tips = new TipModel();
        List<Move> bestMoves = generateBestMoves(difficulty + TIP_DEPTH_HANDICAP, boardState, player);
        if (!bestMoves.isEmpty()) {
            tips.setBestMove(convertMoveListToDto(bestMoves));
            BoardState boardStateAfterMove = checkersGameLogic.performMoves(bestMoves, boardState, false);
            tips.setEnemyRisk(convertMoveListToDto(generateBestMoves(difficulty, boardState, boardStateAfterMove.getCurrentPlayer())));
        }
        return tips;
    }

    private List<Move> generateBestMoves(int depth, BoardState boardState, PieceTypes player) {
        if (checkWinnerService.checkWinner(boardState).isPresent()) {
            return Collections.emptyList();
        }

        List<List<Move>> availableMoves = boardStateCacheService.getAndInsertCache(boardState)
                .getPossibleMoves(boardState.getCurrentPlayer());

        if (availableMoves.size() == 1) {
            return availableMoves.getFirst();
        }
        return miniMax.miniMax(boardState, depth, player).getBestMoveSequence();
    }

    private List<MoveDTO> convertMoveListToDto(List<Move> moves) {
        return moves.stream()
                .map(MoveDTO::new)
                .collect(Collectors.toList());
    }
}