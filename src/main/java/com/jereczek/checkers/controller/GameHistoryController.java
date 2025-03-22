package com.jereczek.checkers.controller;

import com.jereczek.checkers.controller.dto.GameHistoryDto;
import com.jereczek.checkers.model.GameEntity;
import com.jereczek.checkers.model.players.PlayerEntity;
import com.jereczek.checkers.repositories.GameRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/history")
public class GameHistoryController {
    private final GameRepo gameRepo;

    public GameHistoryController(GameRepo gameRepo) {
        this.gameRepo = gameRepo;
    }

    @GetMapping("/{playerID}")
    public ResponseEntity<List<GameHistoryDto>> getGameHistory(@PathVariable String playerID) {
        UUID playerUUID = UUID.fromString(playerID);
        Pageable pageRequest = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "startTime"));

        var history = gameRepo.findGamesByPlayer(playerUUID, pageRequest)
                .getContent()
                .stream()
                .map(game -> new GameHistoryDto(
                        game.getGameID(),
                        getOpponentName(game, playerID),
                        game.getWinner(),
                        game.getStartTime(),
                        game.isPvpGame()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(history);
    }

    private String getOpponentName(GameEntity game, String playerID) {
        if (game.getPlayer1().getPlayerID().toString().equals(playerID)) {
            return game.isPvpGame() ? Optional.ofNullable(game.getPlayer2())
                    .map(PlayerEntity::getUsername)
                    .orElse("N/A") : "CPU";
        } else {
            return game.getPlayer1().getUsername();
        }
    }
}
