package com.ebooks.accrualsaccountingservice.exceptions;

import com.ebooks.accrualsaccountingservice.dtos.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception e){
        ErrorResponse errorResponse = new ErrorResponse("Internal Error", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
