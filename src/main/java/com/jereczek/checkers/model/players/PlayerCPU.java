package com.jereczek.checkers.model.players;

import com.jereczek.checkers.enums.PlayerTypes;

import java.util.UUID;

public class PlayerCPU implements Player {

    public static final UUID CPU_DEFAULT_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final PlayerTypes playerType = PlayerTypes.CPU;

    @Override
    public PlayerTypes getPlayerType() {
        return playerType;
    }
}
