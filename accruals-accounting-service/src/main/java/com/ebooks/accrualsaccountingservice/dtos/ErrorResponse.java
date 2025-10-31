package com.ebooks.accrualsaccountingservice.dtos;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
    private Long timestamp = System.currentTimeMillis();

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
