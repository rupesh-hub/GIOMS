package com.gerp.shared.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum ResponseStatus {
    FAIL,
    SUCCESS;
}
