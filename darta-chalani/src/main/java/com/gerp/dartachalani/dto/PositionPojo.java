package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.enums.PositionType;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PositionPojo {
    private String code;
    private String nameEn;
    private PositionType positionType;
    private String nameNp;
    private Long orderNo;
    private String parentCode;

}