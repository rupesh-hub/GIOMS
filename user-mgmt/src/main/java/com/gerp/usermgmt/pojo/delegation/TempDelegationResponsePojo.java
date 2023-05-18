package com.gerp.usermgmt.pojo.delegation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.usermgmt.pojo.transfer.DetailPojo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TempDelegationResponsePojo {
    private Integer id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime effectiveDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime expireDate;
    private DetailPojo fromSection;
    private DetailPojo fromEmployee;
    private DetailPojo createdBy;
    private DetailPojo toEmployee;
    private DetailPojo toSection;
    private Boolean isActive;
    private Boolean isReassignment;

    //for designation detail
    private DetailPojo fromDesignation;
    private DetailPojo toDesignation;

    private Boolean isAbort;
}
