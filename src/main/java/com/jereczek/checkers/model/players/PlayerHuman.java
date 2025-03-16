package com.jereczek.checkers.model.players;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jereczek.checkers.enums.PlayerTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "player")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlayerHuman implements Player {
    @Transient
    private final PlayerTypes playerType = PlayerTypes.HUMAN;

    @Id
    @Column(name = "id_player")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID playerID;

    @Column(unique = true)
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Override
    public PlayerTypes getPlayerType() {
        return playerType;
    }
}
