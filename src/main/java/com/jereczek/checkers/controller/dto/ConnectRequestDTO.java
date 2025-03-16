package com.jereczek.checkers.controller.dto;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectRequestDTO {
    @Nonnull
    private PlayerDTO player;
    private String gameID;
}
