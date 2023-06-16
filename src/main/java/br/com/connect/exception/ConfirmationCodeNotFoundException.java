package br.com.connect.exception;

public class ConfirmationCodeNotFoundException extends RuntimeException {

    public ConfirmationCodeNotFoundException(String message) {
        super(message);
    }
}
