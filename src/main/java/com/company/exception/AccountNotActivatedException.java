package com.company.exception;

public class AccountNotActivatedException extends RuntimeException{
    public AccountNotActivatedException(String msg){
        super(msg);
    }
}
