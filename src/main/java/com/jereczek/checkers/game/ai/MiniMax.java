package com.jereczek.checkers.game.ai;

import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.game.CheckWinnerService;
import com.jereczek.checkers.game.CheckersGameLogic;
import com.jereczek.checkers.game.Move;
import com.jereczek.checkers.game.ai.cache.BoardStateCacheService;
import com.jereczek.checkers.game.ai.cache.MiniMaxCacheEntry;
import com.jereczek.checkers.game.ai.evaluation.BoardEvaluator;
import com.jereczek.checkers.game.model.board.BoardState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.jereczek.checkers.enums.PieceTypes.WHITE;

@Slf4j
@Service
public class MiniMax {
    private static final Duration TIMEOUT_AFTER = Duration.ofSeconds(30);
    private final CheckersGameLogic checkersGameLogic;

    private final CheckWinnerService checkWinnerService;

    private final BoardStateCacheService cacheService;

    public MiniMax(CheckersGameLogic checkersGameLogic, CheckWinnerService checkWinnerService, BoardStateCacheService cacheService) {
        this.checkersGameLogic = checkersGameLogic;
        this.checkWinnerService = checkWinnerService;
        this.cacheService = cacheService;
    }

    public MiniMaxNode miniMax(BoardState boardState, int depth, PieceTypes maximizingPlayer) {
        Instant timeoutAfter = Instant.now().plus(TIMEOUT_AFTER);
        Instant start = Instant.now();
        MiniMaxNode miniMaxNode = miniMax(boardState, depth, maximizingPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE,
                timeoutAfter);
        log.debug("\nMinimax duration: {}\n", Duration.between(start, Instant.now()));
        return miniMaxNode;
    }

    public MiniMaxNode miniMax(BoardState boardState, int depth, PieceTypes maximizingPlayer, int alpha, int beta,
                               Instant timeoutAfter) {
        if (depth == 0 || Instant.now().isAfter(timeoutAfter)) {
            return new MiniMaxNode(evaluatePosition(boardState, maximizingPlayer), null);
        }
        if (checkWinnerService.checkWinner(boardState).isPresent()) {
            PieceTypes winner = checkWinnerService.checkWinner(boardState).get();
            int score = (winner == maximizingPlayer) ? 20000 : -20000;
            return new MiniMaxNode(score, null);
        }

        MiniMaxCacheEntry cacheEntry = cacheService.getAndInsertCache(boardState);
        List<List<Move>> possibleMoveSequences = cacheEntry.getPossibleMoves(boardState.getCurrentPlayer());

        List<Move> bestMoveSequence = null;
        int bestScore = (boardState.getCurrentPlayer() == maximizingPlayer) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (List<Move> moveSequence : possibleMoveSequences) {
            BoardState newBoardState = new BoardState(boardState);
            newBoardState = checkersGameLogic.performMoves(moveSequence, newBoardState, false);
            MiniMaxNode result = miniMax(newBoardState, depth - 1, maximizingPlayer, alpha, beta, timeoutAfter);
            if (boardState.getCurrentPlayer() == maximizingPlayer) {
                if (result.score > bestScore) {
                    bestScore = result.score;
                    bestMoveSequence = moveSequence;
                    alpha = Math.max(alpha, bestScore);
                    if (beta <= alpha) {
                        break;
                    }
                }
            } else {
                if (result.score < bestScore) {
                    bestScore = result.score;
                    bestMoveSequence = moveSequence;
                    beta = Math.min(beta, bestScore);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
        }
        return new MiniMaxNode(bestScore, bestMoveSequence);
    }

    private Integer evaluatePosition(BoardState boardState, PieceTypes maximizingPlayer) {
        Integer evaluation = cacheService.getOptional(boardState)
                .map(MiniMaxCacheEntry::evaluation)
                .orElse(new BoardEvaluator(boardState).evaluateBoard());
        return maximizingPlayer == WHITE ? evaluation : -evaluation;
    }
}
