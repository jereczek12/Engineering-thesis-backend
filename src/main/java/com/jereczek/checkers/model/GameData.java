package com.jereczek.checkers.model;

import com.jereczek.checkers.game.Move;
import com.jereczek.checkers.movehelper.TipModel;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
public class GameData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Nullable
    @Convert(converter = MovesListConverter.class)
    private List<List<Move>> possibleMoves;

    @Nullable
    private Integer evaluation;
    @Embedded
    private TipModel tips;

    public GameData() {
        this.possibleMoves = new ArrayList<>();
        this.evaluation = 0;
        this.tips = new TipModel();
    }
}
