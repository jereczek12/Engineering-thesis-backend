package com.jereczek.checkers.service;

import com.jereczek.checkers.model.players.PlayerEntity;
import com.jereczek.checkers.repositories.PlayerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepo playerRepo;
    private final PasswordEncoder passwordEncoder;

    public PlayerEntity registerNewPlayer(PlayerEntity playerEntity) {
        playerEntity.setPassword(passwordEncoder.encode(playerEntity.getPassword()));
        return playerRepo.save(playerEntity);
    }

    public PlayerEntity findPlayerByUsername(String username) {
        return playerRepo.findByUsername(username);
    }
}
