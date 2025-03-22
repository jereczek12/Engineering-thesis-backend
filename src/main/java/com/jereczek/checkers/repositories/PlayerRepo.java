package com.jereczek.checkers.repositories;

import com.jereczek.checkers.model.players.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlayerRepo extends JpaRepository<PlayerEntity, UUID> {
    PlayerEntity findByUsername(String username);
}
