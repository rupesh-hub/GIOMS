package com.gerp.templating.entity;

import lombok.Data;

@Data
public class RequestTo {

    private String office;

    private String address;

    private String sectionName;

    private Boolean isSectionName = Boolean.FALSE;

    private String sectionCode;

    private Boolean isGroupName = Boolean.FALSE;
}
