package com.jereczek.checkers.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jereczek.checkers.enums.PieceTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board_state")
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnoreProperties
    private int id;

    @Column(name = "board")
    private String board;

    @Column(name = "moves")
    private String moveList;

    @Column(name = "white_pieces")
    private String whitePieces;

    @Column(name = "black_pieces")
    private String blackPieces;

    @Column(name = "white_kings")
    private String whiteKings;

    @Column(name = "black_kings")
    private String blackKings;

    @Column(name = "current_player")
    private PieceTypes currentPlayer;
}
