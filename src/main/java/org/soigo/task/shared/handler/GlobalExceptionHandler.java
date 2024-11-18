package org.soigo.task.shared.handler;

import io.jsonwebtoken.JwtException;
import org.jetbrains.annotations.NotNull;
import org.soigo.task.shared.handler.dto.ErrorMessage;
import org.soigo.task.task.exception.TaskNotFoundException;
import org.soigo.task.task.exception.UserNotFoundException;
import org.soigo.task.user.exception.AlreadyUserException;
import org.soigo.task.user.exception.EmailNotFountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<?> handlerForbidden(
            @NotNull AccessDeniedException exception
    ) {
        return generateResponse(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({AuthenticationException.class, JwtException.class})
    public ResponseEntity<?> handleAuthenticationException(
            @NotNull Exception exception
    ) {
        return generateResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<?> handlerMethodArgumentTypeMismatchException(
            @NotNull MethodArgumentTypeMismatchException exception
    ) {
        Map<String, String> message = null;
        if (Objects.equals(exception.getRequiredType(), UUID.class)) {
            message = new HashMap<>();
            message.put("Path variable", "invalid UUID format");
        }
        return generateResponse(message, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<?> handlerHttpMessageNotReadableException() {
        return generateResponse("Required request body", HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> handlerMethodArgumentNotValidException(
            @NotNull MethodArgumentNotValidException exception
    ) {

        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> details = new HashMap<>();

        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            details.put(error.getField(), error.getDefaultMessage());
        }

        errorResponse.put("message", "Validation failed");
        errorResponse.put("details", details);

        return generateResponse(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({
            UserNotFoundException.class,
            TaskNotFoundException.class,
            EmailNotFountException.class,
            UsernameNotFoundException.class,
    })
    public ResponseEntity<?> handlerNotFound(
            @NotNull Exception exception
    ) {
        return generateResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            AlreadyUserException.class
    })
    public ResponseEntity<?> handlerConflict(
            @NotNull Exception exception
    ) {
        return generateResponse(exception.getMessage(), HttpStatus.CONFLICT);
    }


    private ResponseEntity<?> generateResponse(
            Object message,
            HttpStatus status
    ) {
        if (message == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity
                .status(status)
                .body(
                        ErrorMessage
                                .builder()
                                .error(message)
                                .build()
                );

    }

}
