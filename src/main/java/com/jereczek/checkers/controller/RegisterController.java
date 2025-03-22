package com.jereczek.checkers.controller;

import com.jereczek.checkers.model.players.PlayerEntity;
import com.jereczek.checkers.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/register")
@CrossOrigin
public class RegisterController {
    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<?> registerPlayer(@RequestBody PlayerEntity playerEntity) {
        playerService.registerNewPlayer(playerEntity);
        return ResponseEntity.ok("Player registered successfully");
    }
}
