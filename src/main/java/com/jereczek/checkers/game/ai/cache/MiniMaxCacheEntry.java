package com.jereczek.checkers.game.ai.cache;

import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.game.Move;

import java.util.List;

public record MiniMaxCacheEntry(Integer evaluation, List<List<Move>> whiteMoves, List<List<Move>> blackMoves) {
    public List<List<Move>> getPossibleMoves(PieceTypes player) {
        return switch (player) {
            case WHITE -> whiteMoves;
            case BLACK -> blackMoves;
            default -> throw new IllegalArgumentException("Requested possible moves for invalid player type");
        };
    }
}
