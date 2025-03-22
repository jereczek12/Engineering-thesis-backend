package com.jereczek.checkers.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jereczek.checkers.enums.GameStatus;
import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.model.players.PlayerEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "game")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String gameID;

    private GameStatus gameStatus;

    @ManyToOne
    @JoinColumn(name = "player_one", nullable = false)
    private PlayerEntity player1;

    @ManyToOne
    @JoinColumn(name = "player_two")
    private PlayerEntity player2;

    @OneToOne
    @JoinColumn(name = "board")
    @JsonIgnoreProperties(value = "id")
    private BoardEntity boardStateEntity;

    @JsonAlias("pvp")
    private boolean isPvpGame;

    @Nullable
    private PieceTypes winner;

    @Nullable
    private Integer difficulty;

    @OneToOne
    private GameData gameData;

    @Column(name = "start_time")
    private Timestamp startTime;
}
