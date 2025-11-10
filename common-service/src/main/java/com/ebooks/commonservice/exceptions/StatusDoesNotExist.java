package com.ebooks.commonservice.exceptions;

public class StatusDoesNotExist extends RuntimeException {
    public StatusDoesNotExist(String message) {
        super(message);
    }
}
