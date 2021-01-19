package com.marufeb.note.model.exceptions;

public class InvalidContentException extends IllegalArgumentException{
    public InvalidContentException(String s) {
        super(s);
    }

    public InvalidContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
