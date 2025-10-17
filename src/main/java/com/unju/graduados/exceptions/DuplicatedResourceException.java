package com.unju.graduados.exceptions;

import lombok.Getter;

@Getter
public class DuplicatedResourceException extends RuntimeException {

    private final String fieldName;

    public DuplicatedResourceException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }
}
