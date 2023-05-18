package com.gerp.templating.entity;

import lombok.Data;

@Data
public class QrEntity {

    private String date;

    private String chalaniNo;

    private String tippaniNo;

    private String letter_number;

    private String subject;

    private String nameNp;

    private String officeName;

    private Long resource_id;

    private String resource_type;

    private String chalaniApprovedDate;

    private String tippaniApprovedDate;

    private String signedEmployeeName;

    private String signedEmployeeDesignation;

    private String status;

}
