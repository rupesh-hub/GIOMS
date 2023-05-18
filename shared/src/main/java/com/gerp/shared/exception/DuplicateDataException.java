package com.gerp.shared.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DuplicateDataException extends RuntimeException {
    private String message;
    private String fieldName;

    public DuplicateDataException(String message, String fieldName) {
        super(message);
        this.message = message;
        this.fieldName = fieldName;
    }

}
