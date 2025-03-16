package com.jereczek.checkers.controller.dto;

import com.jereczek.checkers.game.Move;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.jereczek.checkers.game.model.board.Coordinates.coordinatesToSquareNumber;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MoveDTO {
    private int startPos;
    private int endPos;

    public MoveDTO(Move move) {
        this.startPos = coordinatesToSquareNumber(move.startX(), move.startY());
        this.endPos = coordinatesToSquareNumber(move.endX(), move.endY());
    }
}
