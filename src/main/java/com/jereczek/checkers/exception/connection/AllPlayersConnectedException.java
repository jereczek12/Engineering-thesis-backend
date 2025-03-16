package com.jereczek.checkers.exception.connection;

public class AllPlayersConnectedException extends GameConnectionError {

    public static final String BOTH_PLAYERS_CONNECTED_MESSAGE_TEMPLATE = "Both players are already connected to the game: %s";

    public AllPlayersConnectedException(String gameID) {
        super(String.format(BOTH_PLAYERS_CONNECTED_MESSAGE_TEMPLATE, gameID));
    }

}
