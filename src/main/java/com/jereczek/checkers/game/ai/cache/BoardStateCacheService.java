package com.jereczek.checkers.game.ai.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.game.Move;
import com.jereczek.checkers.game.ai.evaluation.BoardEvaluator;
import com.jereczek.checkers.game.ai.movegenerator.MoveGenerator;
import com.jereczek.checkers.game.model.board.BoardState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class BoardStateCacheService {
    private final Cache<MiniMaxCacheKey, MiniMaxCacheEntry> cache;

    private final MoveGenerator moveGenerator;

    public BoardStateCacheService(Cache<MiniMaxCacheKey, MiniMaxCacheEntry> cache, MoveGenerator moveGenerator) {
        this.cache = cache;
        this.moveGenerator = moveGenerator;
    }

    public MiniMaxCacheEntry getAndInsertCache(BoardState boardState) {
        return cache.get(new MiniMaxCacheKey(boardState.getBoardArray(), boardState.getCurrentPlayer()),
                key -> generateCacheEntry(boardState));
    }

    public Optional<MiniMaxCacheEntry> getOptional(BoardState boardState) {
        return Optional.ofNullable(cache.getIfPresent(new MiniMaxCacheKey(boardState.getBoardArray(), boardState.getCurrentPlayer())));
    }

    private MiniMaxCacheEntry generateCacheEntry(BoardState boardState) {
        PieceTypes originalPlayer = boardState.getCurrentPlayer();

        boardState.setCurrentPlayer(PieceTypes.WHITE);
        List<List<Move>> whiteMoves = moveGenerator.generateMoves(boardState);

        boardState.setCurrentPlayer(PieceTypes.BLACK);
        List<List<Move>> blackMoves = moveGenerator.generateMoves(boardState);

        boardState.setCurrentPlayer(originalPlayer);
        Integer evaluation = evaluatePosition(boardState);

        return new MiniMaxCacheEntry(evaluation, whiteMoves, blackMoves);
    }

    private Integer evaluatePosition(BoardState boardState) {
        BoardEvaluator evaluator = new BoardEvaluator(boardState);
        return evaluator.evaluateBoard();
    }

    public CacheStats getStats() {
        return cache.stats();
    }
}