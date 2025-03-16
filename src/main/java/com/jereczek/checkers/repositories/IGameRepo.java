package com.jereczek.checkers.repositories;

import com.jereczek.checkers.enums.GameStatus;
import com.jereczek.checkers.model.GameEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.UUID;

public interface IGameRepo extends JpaRepository<GameEntity, String> {
    @Query("SELECT g FROM GameEntity g WHERE g.player1.playerID != :uuid" +
            " AND g.player2 IS NULL" +
            " AND g.gameStatus = :gameStatus" +
            " AND g.startTime >= :olderThan " +
            " ORDER BY g.startTime ASC LIMIT 1")
    GameEntity findOldestForRandomConnectionNotOlderThan3Minutes(GameStatus gameStatus, UUID uuid, Timestamp olderThan);

    @Query("SELECT g FROM GameEntity g WHERE g.player1.playerID = :playerId OR g.player2.playerID = :playerId ORDER BY g.startTime DESC")
    Page<GameEntity> findGamesByPlayer(@Param("playerId") UUID playerId, Pageable pageable);
}
