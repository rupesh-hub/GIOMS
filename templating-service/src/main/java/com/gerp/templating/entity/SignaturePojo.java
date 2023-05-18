package com.gerp.templating.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignaturePojo {
    private Long id;
    private String pisCode;
    private String sectionCode;
    private String designationCode;
    private String signatureData;
    private Boolean signatureIsActive;
    private Boolean includeSectionId;
    private String includedSectionId;
    private DesignationPojo designationPojo;
    private Integer delegatedId;
}
