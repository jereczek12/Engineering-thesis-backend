package com.jereczek.checkers.repositories;

import com.jereczek.checkers.model.players.PlayerHuman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IPlayerRepo extends JpaRepository<PlayerHuman, UUID> {
    PlayerHuman findByUsername(String username);
}
