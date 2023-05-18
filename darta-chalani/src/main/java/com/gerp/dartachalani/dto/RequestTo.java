package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestTo {

    private String office;

    private String address;

    private Integer order;

    private String sectionName;

    private Boolean isSectionName = Boolean.FALSE;

    private String sectionCode;

    private Boolean isGroupName;

    private Integer groupId;
}
