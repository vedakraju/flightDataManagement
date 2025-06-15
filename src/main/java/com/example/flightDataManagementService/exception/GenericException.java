package com.example.flightDataManagementService.exception;

import java.util.List;

public class GenericException extends RuntimeException {

    private final String code;
    private final List<FieldError> fieldErrors;

    public GenericException(String code, String message, FieldError... fieldErrors) {
        super(message);
        this.code = code;
        this.fieldErrors = List.of(fieldErrors);
    }

    public String getCode() {
        return code;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

}
