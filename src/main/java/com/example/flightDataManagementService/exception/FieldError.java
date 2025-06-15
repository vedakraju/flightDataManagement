package com.example.flightDataManagementService.exception;

public class FieldError {

    private final String field;
    private final Object rejectedValue;

    public FieldError(String field, Object rejectedValue) {
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

}
