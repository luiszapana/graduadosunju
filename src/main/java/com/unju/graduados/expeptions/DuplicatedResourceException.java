package com.unju.graduados.expeptions;

public class DuplicatedResourceException extends RuntimeException {

    private final String fieldName;

    public DuplicatedResourceException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
