package com.ewallet.springbootewallet.Exceptions;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(long aid) {
        super("Account not found "+ aid);
    }

}
