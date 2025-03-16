package com.jereczek.checkers.service;

import com.jereczek.checkers.model.players.PlayerHuman;
import com.jereczek.checkers.repositories.IPlayerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final IPlayerRepo playerRepo;
    private final PasswordEncoder passwordEncoder;

    public PlayerHuman registerNewPlayer(PlayerHuman playerHuman) {
        playerHuman.setPassword(passwordEncoder.encode(playerHuman.getPassword()));
        return playerRepo.save(playerHuman);
    }

    public PlayerHuman findPlayerByUsername(String username) {
        return playerRepo.findByUsername(username);
    }
}
