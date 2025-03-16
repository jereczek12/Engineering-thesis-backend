package com.jereczek.checkers.model.players;

import com.jereczek.checkers.enums.PlayerTypes;

/***
 * Interface adding layer of abstraction for Players
 */
public interface Player {
    PlayerTypes getPlayerType();
}
