package com.jereczek.checkers.game;

import com.jereczek.checkers.controller.dto.GamePlayDTO;
import com.jereczek.checkers.controller.dto.MoveDTO;
import com.jereczek.checkers.game.model.board.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.jereczek.checkers.game.model.board.Coordinates.coordinatesToSquareNumber;

public record Move(int startX, int startY, int endX, int endY) {

    public static List<Move> fromGameplayDto(GamePlayDTO gamePlayDTO) {
        List<Move> moves = new ArrayList<>();
        for (MoveDTO moveDTO : gamePlayDTO.getMoves()) {
            Coordinates startCords = Coordinates.fromSquareNumber(moveDTO.getStartPos());
            Coordinates endCords = Coordinates.fromSquareNumber(moveDTO.getEndPos());
            Move move = new Move(startCords.x(), startCords.y(), endCords.x(), endCords.y());
            moves.add(move);
        }
        return moves;
    }

    public int startAsSquare() {
        return coordinatesToSquareNumber(startX, startY);
    }

    public int endAsSquare() {
        return coordinatesToSquareNumber(endX, endY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move move)) return false;
        return startX == move.startX && startY == move.startY && endX == move.endX && endY == move.endY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startX, startY, endX, endY);
    }
}
