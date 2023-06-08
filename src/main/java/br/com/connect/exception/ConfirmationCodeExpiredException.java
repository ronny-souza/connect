package br.com.connect.exception;

public class ConfirmationCodeExpiredException extends RuntimeException {

    public ConfirmationCodeExpiredException(String message) {
        super(message);
    }
}
