package com.jereczek.checkers.service;

import com.jereczek.checkers.controller.dto.GamePlayDTO;
import com.jereczek.checkers.controller.dto.GameStartDTO;
import com.jereczek.checkers.controller.dto.PlayerDTO;
import com.jereczek.checkers.enums.GameStatus;
import com.jereczek.checkers.exception.connection.AllPlayersConnectedException;
import com.jereczek.checkers.exception.connection.GameConnectionError;
import com.jereczek.checkers.exception.connection.GameFinishedException;
import com.jereczek.checkers.exception.connection.GameNotFoundException;
import com.jereczek.checkers.game.CheckWinnerService;
import com.jereczek.checkers.game.CheckersGameLogic;
import com.jereczek.checkers.game.GameStateUtils;
import com.jereczek.checkers.game.Move;
import com.jereczek.checkers.game.ai.MiniMax;
import com.jereczek.checkers.game.ai.MiniMaxNode;
import com.jereczek.checkers.game.ai.evaluation.BoardEvaluator;
import com.jereczek.checkers.game.ai.movegenerator.MoveGenerator;
import com.jereczek.checkers.game.model.board.BoardMapper;
import com.jereczek.checkers.game.model.board.BoardState;
import com.jereczek.checkers.model.BoardEntity;
import com.jereczek.checkers.model.GameData;
import com.jereczek.checkers.model.GameEntity;
import com.jereczek.checkers.model.players.PlayerHuman;
import com.jereczek.checkers.movehelper.TipGenerator;
import com.jereczek.checkers.movehelper.TipModel;
import com.jereczek.checkers.repositories.IBoardStateRepo;
import com.jereczek.checkers.repositories.IGameDataRepo;
import com.jereczek.checkers.repositories.IGameRepo;
import com.jereczek.checkers.repositories.IPlayerRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jereczek.checkers.enums.PieceTypes.WHITE;
import static com.jereczek.checkers.model.players.PlayerCPU.CPU_DEFAULT_UUID;

@Slf4j
@Service
@Transactional
public class GameService {
    private final IGameRepo gameRepo;

    private final IPlayerRepo playerRepo;

    private final IBoardStateRepo boardStateRepo;

    private final IGameDataRepo gameDataRepo;

    private final CheckersGameLogic checkersGameLogic;

    private final MoveGenerator moveGenerator;

    private final TipGenerator tipGenerator;

    private final MiniMax miniMax;

    private final CheckWinnerService checkWinnerService;

    public GameService(IGameRepo gameRepo, IPlayerRepo playerRepo, IBoardStateRepo boardStateRepo, IGameDataRepo gameDataRepo, CheckersGameLogic checkersGameLogic, MoveGenerator moveGenerator, TipGenerator tipGenerator, MiniMax miniMax, CheckWinnerService checkWinnerService) {
        this.gameRepo = gameRepo;
        this.playerRepo = playerRepo;
        this.boardStateRepo = boardStateRepo;
        this.gameDataRepo = gameDataRepo;
        this.checkersGameLogic = checkersGameLogic;
        this.moveGenerator = moveGenerator;
        this.miniMax = miniMax;
        this.tipGenerator = tipGenerator;
        this.checkWinnerService = checkWinnerService;
    }

    public GameEntity createGame(GameStartDTO startDTO) {
        PlayerHuman player = playerRepo.findById(UUID.fromString(startDTO.getPlayerID())).get();
        BoardEntity boardEntity = BoardMapper.updateEntityFromBoardState(new BoardEntity(), new BoardState());
        boardStateRepo.save(boardEntity);

        GameEntity game = GameEntity.builder()
                .boardStateEntity(boardEntity)
                .player1(player)
                .isPvpGame(startDTO.isPvp())
                .gameStatus(GameStatus.NEW)
                .gameData(gameDataRepo.save(new GameData()))
                .startTime(Timestamp.from(Instant.now()))
                .build();
        if (!startDTO.isPvp()) {
            game.setPlayer2(playerRepo.findById(CPU_DEFAULT_UUID).get());
            game.setGameStatus(GameStatus.IN_PROGRESS);
            game.setDifficulty(Optional.ofNullable(startDTO.getDifficulty()).orElse(5));
        }
        gameRepo.save(game);
        return game;
    }

    public GameEntity getGameState(String gameID) throws GameNotFoundException {
        return gameRepo
                .findById(gameID)
                .orElseThrow(() -> new GameNotFoundException(gameID));
    }

    public GameEntity connectToPvpGame(PlayerDTO player2, String gameID) throws GameConnectionError {
        if (!gameRepo.existsById(gameID)) {
            throw new GameNotFoundException(gameID);
        }
        GameEntity game = gameRepo.findById(gameID).get();
        if (game.getPlayer2() != null) {
            throw new AllPlayersConnectedException(gameID);
        }
        if (game.getGameStatus().equals(GameStatus.FINISHED)) {
            log.debug("Tried to connect to game: {}, but it's already finished!", game.getGameID());
            throw new GameFinishedException();
        }
        if (!game.isPvpGame()) {
            throw new GameConnectionError("Game is not a pvp game!");
        }
        PlayerHuman player = playerRepo.findById(UUID.fromString(player2.getPlayerID())).get();
        game.setPlayer2(player);
        game.setGameStatus(GameStatus.IN_PROGRESS);
        gameRepo.save(game);
        return game;
    }

    public GameEntity connectToRandomGame(PlayerDTO player2) {
        PlayerHuman player = playerRepo.findById(UUID.fromString(player2.getPlayerID())).get();

        GameEntity game = gameRepo.findOldestForRandomConnectionNotOlderThan3Minutes(GameStatus.NEW, UUID.fromString(player2.getPlayerID()),
                Timestamp.from(Instant.now().minus(3, ChronoUnit.MINUTES)));
        game.setPlayer2(player);
        game.setGameStatus(GameStatus.IN_PROGRESS);
        gameRepo.save(game);
        return game;
    }

    public GameEntity gamePlayPvp(GamePlayDTO gamePlayDTO) {
        GameEntity game = gameRepo.findById(gamePlayDTO.getGameID())
                .orElseThrow(() -> new GameNotFoundException(gamePlayDTO.getGameID()));
        BoardState originalBoardState = GameStateUtils.validateAndGetBoardState(game, gamePlayDTO);

        List<Move> moves = Move.fromGameplayDto(gamePlayDTO);
        BoardState boardState = processMove(originalBoardState, moves);
        game.setBoardStateEntity(BoardMapper.updateEntityFromBoardState(game.getBoardStateEntity(), boardState));
        updateAvailableMovesForPvp(boardState, game);
        checkWinnerService.checkWinner(boardState).ifPresent(game::setWinner);
        gameRepo.save(game);

        return game;
    }

    public GameEntity gamePlayCpu(GameEntity game, int depth) {
        BoardState originalBoardState = BoardMapper.entityToBoardStateMapper(game.getBoardStateEntity());

        MiniMaxNode cpuMove = miniMax.miniMax(originalBoardState, depth, originalBoardState.getCurrentPlayer());

        var updatedBoardState = processMove(originalBoardState, cpuMove.getBestMoveSequence());
        checkWinnerService.checkWinner(updatedBoardState).ifPresent(game::setWinner);
        game.setBoardStateEntity(BoardMapper.updateEntityFromBoardState(game.getBoardStateEntity(), updatedBoardState));
        GameData gameData = updateGameData(updatedBoardState, game);
        game.setGameData(gameData);
        gameRepo.save(game);

        return game;
    }

    public GameEntity generateAndUpdateTips(String gameId) {
        GameEntity game = gameRepo.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));
        BoardState boardState = BoardMapper.entityToBoardStateMapper(game.getBoardStateEntity());

        TipModel tips = tipGenerator.generateTips(
                Optional.ofNullable(game.getDifficulty()).orElse(5),
                boardState, WHITE
        );

        GameData gameData = game.getGameData();
        gameData.setTips(tips);
        game.setGameData(gameData);
        gameRepo.save(game);
        return game;
    }

    private BoardState processMove(BoardState boardState, List<Move> moves) {
        return checkersGameLogic.performMoves(moves, boardState, true);
    }

    private GameData updateGameData(BoardState boardState, GameEntity game) {
        GameData gameData = Optional.ofNullable(game.getGameData()).get();
        Integer evaluation = new BoardEvaluator(boardState).evaluateBoard();
        gameData.setPossibleMoves(moveGenerator.generateMoves(boardState));
        gameData.setEvaluation(evaluation);
        return gameData;
    }

    private GameData updateAvailableMovesForPvp(BoardState boardState, GameEntity game) {
        GameData gameData = Optional.ofNullable(game.getGameData()).orElseGet(() -> {
            GameData newData = new GameData();
            return gameDataRepo.save(newData);
        });

        List<List<Move>> availableMoves = moveGenerator.generateMoves(boardState);
        gameData.setPossibleMoves(availableMoves);

        return gameDataRepo.save(gameData);
    }
}
