package com.jereczek.checkers.game;

import com.jereczek.checkers.controller.dto.GamePlayDTO;
import com.jereczek.checkers.enums.GameStatus;
import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.exception.IllegalMoveException;
import com.jereczek.checkers.exception.IllegalPlayerException;
import com.jereczek.checkers.exception.connection.GameFinishedException;
import com.jereczek.checkers.game.model.board.BoardMapper;
import com.jereczek.checkers.game.model.board.BoardState;
import com.jereczek.checkers.model.GameEntity;
import lombok.extern.slf4j.Slf4j;

import static com.jereczek.checkers.enums.PieceTypes.WHITE;

@Slf4j
public class GameStateUtils {
    public static PieceTypes getNextPlayerType(PieceTypes currentPlayer) {
        return currentPlayer.equals(PieceTypes.WHITE) ? PieceTypes.BLACK : PieceTypes.WHITE;
    }

    public static BoardState validateAndGetBoardState(GameEntity game, GamePlayDTO gamePlayDTO) {
        validateGameStatus(game);

        BoardState boardState = BoardMapper.entityToBoardStateMapper(game.getBoardStateEntity());
        PieceTypes expectedType = game.getBoardStateEntity().getCurrentPlayer();
        var expectedPlayer = expectedType.equals(WHITE) ? game.getPlayer1() : game.getPlayer2();
        validateCorrectPlayerTurn(boardState, gamePlayDTO, expectedPlayer.getPlayerID().toString(), expectedType);
        return boardState;
    }

    public static void validateCorrectPlayerTurn(BoardState boardState,
                                                 GamePlayDTO gamePlayDTO,
                                                 String expectedPlayerId,
                                                 PieceTypes expectedType) {
        if (!expectedPlayerId.equals(gamePlayDTO.getPlayerID())) {
            throw new IllegalPlayerException(String.format("Received move for %s player, but received: %s", expectedPlayerId, gamePlayDTO.getPlayerID()));
        }
        int startPos = gamePlayDTO.getMoves().getFirst().getStartPos();
        var isCorrectPlayerType = expectedType.equals(WHITE) ? boardState.getWhitePieces().containsKey(startPos) :
                boardState.getBlackPieces().containsKey(startPos);
        if (!isCorrectPlayerType) {
            throw new IllegalMoveException(String.format("It's %s player turn, received turn for opponents", expectedType.name()));
        }
    }

    public static void validateGameStatus(GameEntity game) throws GameFinishedException {
        if (game.getGameStatus().equals(GameStatus.FINISHED)) {
            log.debug("Tried to connect to game: {}, but it's already finished!", game.getGameID());
            throw new GameFinishedException();
        }
    }
}
