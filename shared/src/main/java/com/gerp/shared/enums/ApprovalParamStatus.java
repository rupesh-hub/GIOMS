package com.gerp.shared.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ApprovalParamStatus {

    update,
    approve,
    revert,
    review;
}
