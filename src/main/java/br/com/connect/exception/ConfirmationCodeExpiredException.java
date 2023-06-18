package br.com.connect.exception;

public class ConfirmationCodeExpiredException extends Exception {

    public ConfirmationCodeExpiredException(Exception e) {
        super(e);
    }

    public ConfirmationCodeExpiredException(String message) {
        super(message);
    }
}
