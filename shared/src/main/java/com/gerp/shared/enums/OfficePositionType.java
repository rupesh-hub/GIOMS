package com.gerp.shared.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OfficePositionType {
    OFFICE_ADMIN,
    OFFICE_HEAD;
}