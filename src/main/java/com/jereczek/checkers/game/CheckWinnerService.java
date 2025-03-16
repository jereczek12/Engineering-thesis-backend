package com.jereczek.checkers.game;

import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.game.ai.cache.BoardStateCacheService;
import com.jereczek.checkers.game.model.board.BoardState;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.jereczek.checkers.enums.PieceTypes.BLACK;
import static com.jereczek.checkers.enums.PieceTypes.WHITE;

@Component
public class CheckWinnerService {
    private final BoardStateCacheService boardStateCacheService;

    public CheckWinnerService(BoardStateCacheService boardStateCacheService) {
        this.boardStateCacheService = boardStateCacheService;
    }


    public Optional<PieceTypes> checkWinner(BoardState boardState) {
        if (boardState.getWhitePieces().isEmpty()) {
            return Optional.of(BLACK);
        }
        if (boardState.getBlackPieces().isEmpty()) {
            return Optional.of(WHITE);
        }
        List<List<Move>> availableMoves = boardStateCacheService.getAndInsertCache(boardState)
                .getPossibleMoves(boardState.getCurrentPlayer());
        if (availableMoves.isEmpty()) {
            return Optional.of(boardState.getCurrentPlayer() == WHITE ? BLACK : WHITE);
        }
        return Optional.empty();
    }
}
