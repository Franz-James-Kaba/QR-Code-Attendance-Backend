package com.example.attendance_system.exceptions;

import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(TokenExpiredException exception) {
        var error = ErrorResponse.builder()
                .message(exception.getMessage())
                .code(BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(InvalidTokenException exception) {
        var error = ErrorResponse.builder()
                .message(exception.getMessage())
                .code(BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(AuthenticationException exception) {
        var error = ErrorResponse.builder()
                .message("Invalid username or password")
                .code(UNAUTHORIZED.value())
                .build();
        return new ResponseEntity<>(error, UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(ResourceNotFoundException exception) {
        var error = ErrorResponse.builder()
                .message(exception.getMessage())
                .code(NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(error, NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(SignatureException ex) {
        var error = ErrorResponse.builder()
                .message(ex.getMessage())
                .code(UNAUTHORIZED.value())
                .build();
        return new ResponseEntity<>(error, UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

}
