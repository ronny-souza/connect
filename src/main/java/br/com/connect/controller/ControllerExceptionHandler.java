package br.com.connect.controller;

import br.com.connect.exception.ConfirmationCodeExpiredException;
import br.com.connect.exception.ConfirmationCodeNotFoundException;
import br.com.connect.exception.UnauthenticatedUserException;
import br.com.connect.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ConfirmationCodeNotFoundException.class)
    public ResponseEntity<String> handleConfirmationCodeNotFoundException(ConfirmationCodeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ConfirmationCodeExpiredException.class)
    public ResponseEntity<String> handleConfirmationCodeExpiredException(ConfirmationCodeExpiredException ex) {
        return ResponseEntity.status(HttpStatus.GONE).body(ex.getMessage());
    }

    @ExceptionHandler(UnauthenticatedUserException.class)
    public ResponseEntity<String> handleUnauthenticatedUserException(UnauthenticatedUserException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
