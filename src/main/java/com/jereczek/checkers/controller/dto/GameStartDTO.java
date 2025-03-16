package com.jereczek.checkers.controller.dto;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class GameStartDTO {
    private String playerID;
    private boolean pvp;
    @Nullable
    private Integer difficulty;
}
