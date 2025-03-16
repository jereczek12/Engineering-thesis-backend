package com.jereczek.checkers.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class GamePlayDTO {
    private List<MoveDTO> moves;
    private String gameID;
    private String playerID;
}
