package com.ebooks.commonmoduleloan.exceptions;

import com.ebooks.commonmoduleloan.dtos.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e){
        ErrorResponse errorResponse = new ErrorResponse("Internal server error", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
