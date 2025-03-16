package com.jereczek.checkers.game.ai.evaluation;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PositionsConstants {
    public static final Set<Integer> BLACK_BACK_ROW = Stream.of(46, 47, 48, 49, 50)
            .collect(Collectors.toCollection(HashSet::new));

    public static final Set<Integer> WHITE_BACK_ROW = Stream.of(1, 2, 3, 4, 5)
            .collect(Collectors.toCollection(HashSet::new));

    public static final Set<Integer> CENTER_SQUARES = Stream.of(28, 29, 22, 23, 27, 24)
            .collect(Collectors.toCollection(HashSet::new));

    public static final Set<Integer> SAFE_COLUMNS = Stream.of(5, 15, 25, 35, 45,
            6, 16, 26, 36, 46).collect(Collectors.toCollection(HashSet::new));
}
