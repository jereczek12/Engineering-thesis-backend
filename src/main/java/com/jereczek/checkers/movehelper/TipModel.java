package com.jereczek.checkers.movehelper;

import com.jereczek.checkers.controller.dto.MoveDTO;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Embeddable
public class TipModel {
    @Convert(converter = TipsAttributeConverter.class)
    private List<MoveDTO> bestMove;
    @Convert(converter = TipsAttributeConverter.class)
    private List<MoveDTO> enemyRisk;

    public TipModel() {
        this.bestMove = new ArrayList<>();
        this.enemyRisk = new ArrayList<>();
    }
}
