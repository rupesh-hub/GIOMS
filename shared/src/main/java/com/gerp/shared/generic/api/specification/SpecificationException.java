package com.gerp.shared.generic.api.specification;

public class SpecificationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SpecificationException() {
    }

    public SpecificationException(String string) {
        super(string);
    }

    public SpecificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpecificationException(Throwable cause) {
        super(cause);
    }

}
