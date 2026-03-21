package com.gobuddy.exception;

import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── Validation errors (e.g., @NotBlank, @Email) ─────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(
                ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Validation failed", String.join(", ", errors))
        );
    }

    // ─── Custom application errors ────────────────────────────────────────────
    @ExceptionHandler(GoBuddyException.class)
    public ResponseEntity<ErrorResponse> handleGoBuddy(GoBuddyException ex) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null)
        );
    }

    // ─── Wrong credentials ────────────────────────────────────────────────────
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponse.of(401, "Invalid email or password.", null)
        );
    }

    // ─── Access denied (403) ──────────────────────────────────────────────────
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse.of(403, "You do not have permission to access this resource.", null)
        );
    }

    // ─── Entity not found ─────────────────────────────────────────────────────
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of(404, ex.getMessage(), null)
        );
    }

    // ─── Fallback for all other exceptions ────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.of(500, "An unexpected error occurred. Please try again.", ex.getMessage())
        );
    }

    // ─── Error response structure ─────────────────────────────────────────────
    @lombok.Data
    @lombok.AllArgsConstructor(staticName = "of")
    public static class ErrorResponse {
        private int status;
        private String message;
        private String details;
        private LocalDateTime timestamp = LocalDateTime.now();

        public static ErrorResponse of(int status, String message, String details) {
            ErrorResponse er = new ErrorResponse();
            er.status    = status;
            er.message   = message;
            er.details   = details;
            er.timestamp = LocalDateTime.now();
            return er;
        }

        public ErrorResponse() {}
    }
}
