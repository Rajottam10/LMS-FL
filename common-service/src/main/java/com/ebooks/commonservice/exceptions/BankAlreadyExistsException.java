package com.ebooks.commonservice.exceptions;

public class BankAlreadyExistsException extends RuntimeException{
    public BankAlreadyExistsException(String message){
        super(message);
    }
}
