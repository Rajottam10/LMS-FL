package com.ebooks.commonservice.exceptions;

import com.ebooks.commonservice.dtos.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BankAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleBankAlreadyExistsException(BankAlreadyExistsException exception){
        ErrorResponse error = new ErrorResponse("Bank already exists", exception.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException exception){
        ErrorResponse error = new ErrorResponse("Username already exists", exception.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException exception){
        ErrorResponse error = new ErrorResponse("Email already exists", exception.getMessage());
        return ResponseEntity.badRequest().body(error);    }

    @ExceptionHandler(StatusDoesNotExist.class)
    public ResponseEntity<ErrorResponse> handleStatusDoesNotExistsException(StatusDoesNotExist exception){
        ErrorResponse error = new ErrorResponse("Status doesn't exists", exception.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception exception){
        ErrorResponse error = new ErrorResponse("Internal server error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
