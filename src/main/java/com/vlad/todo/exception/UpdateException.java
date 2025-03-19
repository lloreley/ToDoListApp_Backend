package com.vlad.todo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UpdateException extends RuntimeException {
    public UpdateException(String message) {
        super(message);
    }
}