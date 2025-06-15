package com.example.flightDataManagementService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ErrorResponse> handleGenericException(GenericException ex) {
        HttpStatus status = switch (ex.getCode()) {
            case "BAD_REQUEST" -> HttpStatus.BAD_REQUEST;
            case "UNAUTHORIZED" -> HttpStatus.UNAUTHORIZED;
            case "FORBIDDEN" -> HttpStatus.FORBIDDEN;
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return ResponseEntity.status(status)
                             .body(new ErrorResponse(ex.getCode(), ex.getMessage(), ex.getFieldErrors()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new ErrorResponse(
                                     "BAD_REQUEST",
                                     "Malformed request body: " + ex.getMostSpecificCause().getMessage(),
                                     null
                             ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(DataIntegrityViolationException ex) {
        String message = "Flight with the same airline, departure airport, and departure time already exists.";

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(new ErrorResponse(
                                     "CONFLICT",
                                     message,
                                     List.of(new FieldError("flightKey", "duplicate combination of airline, departureAirport, departureTime"))
                             ));
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParam(MissingServletRequestParameterException ex) {
        String paramName = ex.getParameterName();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new ErrorResponse(
                                     "BAD_REQUEST",
                                     "Required parameter '" + paramName + "' is not present.",
                                     List.of(new FieldError(paramName, null))
                             ));
    }

    public static class ErrorResponse {

        public final String code;
        public final String message;
        public final Object errors;

        public ErrorResponse(String code, String message, Object errors) {
            this.code = code;
            this.message = message;
            this.errors = errors;
        }

    }

}
