package com.jereczek.checkers.exception.connection;

public class GameFinishedException extends GameConnectionError {
    private static final String GAME_FINISHED_EXCEPTION_MESSAGE = "Game is already finished!";

    public GameFinishedException() {
        super(GAME_FINISHED_EXCEPTION_MESSAGE);
    }
}
