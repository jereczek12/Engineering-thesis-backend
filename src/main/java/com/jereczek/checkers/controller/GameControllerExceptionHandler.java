package com.jereczek.checkers.controller;

import com.jereczek.checkers.exception.IllegalMoveException;
import com.jereczek.checkers.exception.IllegalPlayerException;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GameControllerExceptionHandler {

    @ExceptionHandler(value = IllegalPlayerException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage message(IllegalPlayerException exception) {
        return new ErrorMessage(exception);
    }

    @ExceptionHandler(value = IllegalMoveException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage message(IllegalMoveException exception) {
        return new ErrorMessage(exception);
    }
}
