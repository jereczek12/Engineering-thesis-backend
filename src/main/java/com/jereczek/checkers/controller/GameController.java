package com.jereczek.checkers.controller;

import com.jereczek.checkers.controller.dto.ConnectRequestDTO;
import com.jereczek.checkers.controller.dto.GamePlayDTO;
import com.jereczek.checkers.controller.dto.GameStartDTO;
import com.jereczek.checkers.exception.IllegalPlayerException;
import com.jereczek.checkers.exception.connection.AllPlayersConnectedException;
import com.jereczek.checkers.exception.connection.GameFinishedException;
import com.jereczek.checkers.exception.connection.GameNotFoundException;
import com.jereczek.checkers.model.GameEntity;
import com.jereczek.checkers.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static com.jereczek.checkers.config.WebsocketConfiguration.GAME_PROGRESS_TOPIC;
import static com.jereczek.checkers.config.WebsocketConfiguration.GAME_TIPS_TOPIC;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameController(GameService gameService, SimpMessagingTemplate simpMessagingTemplate, ExecutorService asyncCpuExecutorService) {
        this.gameService = gameService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostMapping("/start")
    public ResponseEntity<GameEntity> startGame(@RequestBody @Validated GameStartDTO startDTO) {
        log.debug("Start game request with player: {}", startDTO);
        GameEntity game = gameService.createGame(startDTO);
        simpMessagingTemplate.convertAndSend(GAME_PROGRESS_TOPIC + game.getGameID(), game);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/connect")
    public ResponseEntity<GameEntity> connectToGame(@RequestBody @Validated ConnectRequestDTO connectRequest)
            throws AllPlayersConnectedException, GameNotFoundException, GameFinishedException {
        GameEntity game;
        if (connectRequest.getGameID() != null) {
            log.debug("Trying to connect to game: {}", connectRequest.getGameID());
            game = gameService.connectToPvpGame(connectRequest.getPlayer(), connectRequest.getGameID());
            simpMessagingTemplate.convertAndSend(GAME_PROGRESS_TOPIC + game.getGameID(), game);
        } else {
            log.debug("Trying to connect to a random game");
            game = gameService.connectToRandomGame(connectRequest.getPlayer());
            simpMessagingTemplate.convertAndSend(GAME_PROGRESS_TOPIC + game.getGameID(), game);
        }
        return ResponseEntity.ok(game);
    }

    @PostMapping("/gameplay")
    public ResponseEntity<GameEntity> makeMove(@RequestBody @Validated GamePlayDTO request) throws GameFinishedException, GameNotFoundException, IllegalPlayerException {
        log.debug("Making a move: {}", request);

        GameEntity afterPlayer = gameService.gamePlayPvp(request);
        simpMessagingTemplate.convertAndSend(GAME_PROGRESS_TOPIC + afterPlayer.getGameID(), afterPlayer);
        if (!afterPlayer.isPvpGame()) {
            runCpuMoveAsync(afterPlayer);
        } else {
            simpMessagingTemplate.convertAndSend(GAME_TIPS_TOPIC + afterPlayer.getGameID(), afterPlayer.getGameData());
        }
        return ResponseEntity.ok(afterPlayer);
    }

    @Async
    public void runCpuMoveAsync(GameEntity afterPlayer) {
        GameEntity game = gameService.gamePlayCpu(afterPlayer, Optional.ofNullable(afterPlayer.getDifficulty()).orElse(3));
        simpMessagingTemplate.convertAndSend(GAME_PROGRESS_TOPIC + game.getGameID(), game);
        var gameEntityWithTips = gameService.generateAndUpdateTips(game.getGameID());
        simpMessagingTemplate.convertAndSend(GAME_TIPS_TOPIC + gameEntityWithTips.getGameID(), gameEntityWithTips);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameEntity> getGame(@PathVariable String id) {
        try {
            GameEntity game = gameService.getGameState(id);
            simpMessagingTemplate.convertAndSend(GAME_PROGRESS_TOPIC + game.getGameID(), game);
            return ResponseEntity.ok(game);
        } catch (GameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found", e);
        }
    }
}
