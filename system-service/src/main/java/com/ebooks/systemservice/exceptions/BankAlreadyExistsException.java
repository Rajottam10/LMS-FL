package com.ebooks.systemservice.exceptions;

public class BankAlreadyExistsException extends RuntimeException{
    public BankAlreadyExistsException(String message){
        super(message);
    }
}
