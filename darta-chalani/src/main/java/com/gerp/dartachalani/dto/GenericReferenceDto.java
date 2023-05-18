package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericReferenceDto {
    private Long id;
    private Long referenceId;
    private String pisCode;
    private String officeCode;
    private Boolean include;
    private Boolean referenceIsEditable;
}
