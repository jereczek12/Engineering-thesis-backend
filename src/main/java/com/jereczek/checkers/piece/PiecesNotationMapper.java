package com.jereczek.checkers.piece;

import com.jereczek.checkers.enums.PieceTypes;
import com.jereczek.checkers.game.model.board.Coordinates;

import java.util.HashMap;
import java.util.Map;

public class PiecesNotationMapper {
    public static HashMap<Integer, Piece> convertStringToPieces(String notation) {
        HashMap<Integer, Piece> pieces = new HashMap<>();
        String[] pieceNotations = notation.split(";");

        for (String pieceNotation : pieceNotations) {
            String[] parts = pieceNotation.split("-");
            int type = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int x = Integer.parseInt(parts[2]);

            Piece piece = new Piece(x, y, PieceTypes.valueOfFigure(type));
            pieces.put(Coordinates.coordinatesToSquareNumber(x, y), piece);
        }
        return pieces;
    }

    public static String[] convertPiecesToString(Map<Integer, PieceTypes> pieces) {
        StringBuilder regular = new StringBuilder();
        StringBuilder kings = new StringBuilder();
        pieces.forEach((square, type) -> {
            Coordinates coords = Coordinates.fromSquareNumber(square);
            switch (type) {
                case WHITE, BLACK -> {
                    regular.append(coords.x());
                    regular.append("-");
                    regular.append(coords.y());
                    regular.append(";");
                }
                case WHITE_KING, BLACK_KING -> {
                    kings.append(coords.x());
                    kings.append("-");
                    kings.append(coords.y());
                    kings.append(";");
                }
            }
        });

        return new String[]{regular.toString(), kings.toString()};
    }

}
