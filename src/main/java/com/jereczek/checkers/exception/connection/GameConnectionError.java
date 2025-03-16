package com.jereczek.checkers.exception.connection;

public class GameConnectionError extends RuntimeException {
    public GameConnectionError(String message) {
        super(message);
    }
}
