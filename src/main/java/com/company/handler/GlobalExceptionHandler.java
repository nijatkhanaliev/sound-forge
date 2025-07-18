package com.company.handler;

import com.company.exception.AccountNotActivatedException;
import com.company.exception.AlreadyExistsException;
import com.company.exception.NotFoundException;
import com.company.exception.OperationNotPermittedException;
import com.company.exception.TokenNotValidException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgument(
            IllegalArgumentException ex) {
        log.error("IllegalArgumentException happened, message: {}", ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .errorMessage(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUsernameNotFound(
            UsernameNotFoundException ex) {
        log.error("UsernameNotFoundException happened, message: {}", ex.getMessage());
        return ResponseEntity.status(NOT_FOUND)
                .body(
                        ExceptionResponse.builder()
                                .errorMessage(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handle(AlreadyExistsException ex) {
        log.error("AlreadyExistsException happened, message: {}", ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .errorMessage(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(TokenNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleTokenNotValid(
            TokenNotValidException ex) {
        log.error("TokenNotValid happened, message: {}", ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .errorMessage(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleMessaging(MessagingException ex) {
        log.error("MessagingException happened, message: {}", ex.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .errorMessage(ex.getMessage())
                                .build()
                );
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgument(
            MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException happened, message: {}", ex.getMessage());
        Set<String> validationErrors = new HashSet<>();

        ex.getBindingResult().getAllErrors()
                .forEach((e) -> {
                    String message = e.getDefaultMessage();
                    validationErrors.add(message);
                });


        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(validationErrors)
                                .build()
                );
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleOperationNotPermitted(
            OperationNotPermittedException ex) {
        log.error("OperationNotPermittedException happened, message: {}", ex.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .errorMessage(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<ExceptionResponse> handleAccountNotActivated(
            AccountNotActivatedException ex) {
        log.error("AccountNotActivatedException happened, message: {}", ex.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .errorMessage(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundException(
            NotFoundException ex) {
        log.error("NotFoundException happened, message: {}", ex.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .errorMessage(ex.getMessage())
                                .build()
                );
    }


}
