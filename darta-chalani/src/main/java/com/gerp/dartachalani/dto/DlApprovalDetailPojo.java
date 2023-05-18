package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DlApprovalDetailPojo {

    private Long detailsId;
    private String description;
    private String detailsStatus;
    private String detailsIsActive;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Timestamp createdDate;
    private String createdDateNp;
}
