package com.jereczek.checkers.game.ai.evaluation;

public class EvaluationConstants {
    public static final int PIECE_VALUE = 100;

    public static final int KING_VALUE = 200;

    public static final int BACK_ROW_PIECE_BONUS = 20;

    public static final int SAFE_COLUMN_BONUS = 30;

    public static final int FORMATION_BONUS = 25;

    public static final int ADVANCEMENT_BONUS = 10;

    public static final int ENDGAME_CENTER_BONUS = 25;

    public static final float CENTER_MODIFIER = 1.5F;

    public static final float DANGER_MODIFIER = 0.3F;

    public static final float ENDGAME_KING_MODIFIER = 1.7f;

    public static final float ENDGAME_KING_CENTER_MODIFIER = 1.4f;
}
