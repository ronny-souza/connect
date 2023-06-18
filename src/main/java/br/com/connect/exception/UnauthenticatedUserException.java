package br.com.connect.exception;

public class UnauthenticatedUserException extends RuntimeException {

    public UnauthenticatedUserException(String message) {
        super(message);
    }
}
