package com.jereczek.checkers.exception.connection;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameNotFoundException extends GameConnectionError {
    private final static String GAME_NOT_FOUND_EXCEPTION_TEMPLATE = "Game with id: %s not found";

    public GameNotFoundException(String gameID) {
        super(String.format(GAME_NOT_FOUND_EXCEPTION_TEMPLATE, gameID));
    }
}
