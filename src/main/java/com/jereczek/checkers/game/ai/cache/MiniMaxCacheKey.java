package com.jereczek.checkers.game.ai.cache;

import com.jereczek.checkers.enums.PieceTypes;

import java.util.Arrays;
import java.util.Objects;

public record MiniMaxCacheKey(int[][] boardArray, PieceTypes currentPlayer) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MiniMaxCacheKey that = (MiniMaxCacheKey) o;
        return Arrays.deepEquals(boardArray, that.boardArray) &&
                currentPlayer == that.currentPlayer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(boardArray), currentPlayer);
    }
}
