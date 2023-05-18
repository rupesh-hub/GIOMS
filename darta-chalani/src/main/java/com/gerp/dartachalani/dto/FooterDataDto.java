package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FooterDataDto {

    private Long id;
    private String footer;
    private String officeCode;
    private Boolean isActive;
    private String template;

}
