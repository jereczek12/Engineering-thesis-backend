package com.jereczek.checkers.game.ai;

import com.jereczek.checkers.game.Move;
import lombok.Data;

import java.util.List;

@Data
public class MiniMaxNode {
    int score;
    List<Move> bestMoveSequence;

    public MiniMaxNode(int score, List<Move> bestMoveSequence) {
        this.score = score;
        this.bestMoveSequence = bestMoveSequence;
    }
}
