package com.jereczek.checkers.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum PieceTypes {
    EMPTY(0), WHITE(1), BLACK(2), WHITE_KING(7), BLACK_KING(8), CAPTURED(11);

    private static final Map<Integer, PieceTypes> BY_VALUE = new HashMap<>();

    static {
        for (PieceTypes e : values()) {
            BY_VALUE.put(e.value, e);
        }
    }

    private final Integer value;

    PieceTypes(Integer value) {
        this.value = value;
    }

    public static PieceTypes valueOfFigure(Integer value) {
        return BY_VALUE.get(value);
    }

    public boolean isRegularPiece() {
        return this == PieceTypes.WHITE || this == PieceTypes.BLACK;
    }

    public boolean isKingPiece() {
        return this == PieceTypes.WHITE_KING || this == PieceTypes.BLACK_KING;
    }
}
