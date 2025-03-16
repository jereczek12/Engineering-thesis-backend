package com.jereczek.checkers.piece;

import com.jereczek.checkers.enums.PieceTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Piece {
    private int positionX;
    private int positionY;
    private PieceTypes type;
}
