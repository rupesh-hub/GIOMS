package com.gerp.dartachalani.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SenderEmployeeDetail {
    private String date;

    private String employeeName;

    private String employeeDesignation;

    private String employeeSection;
}
