package com.jereczek.checkers.controller.dto;

import com.jereczek.checkers.enums.PieceTypes;

import java.sql.Timestamp;

public record GameHistoryDto(
        String gameID,
        String opponent,
        PieceTypes winner,
        Timestamp startTime,
        boolean wasPvpGame
) {
}
