package com.greem4.springmedicines.exception;

public class PasswordTooShortException extends Throwable {
    public PasswordTooShortException(String message) {
        super(message);
    }
}
