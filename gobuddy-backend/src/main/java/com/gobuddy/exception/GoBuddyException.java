package com.gobuddy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GoBuddyException extends RuntimeException {
    public GoBuddyException(String message) {
        super(message);
    }
}
