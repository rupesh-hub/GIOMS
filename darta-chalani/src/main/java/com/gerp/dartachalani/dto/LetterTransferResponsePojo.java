package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LetterTransferResponsePojo {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
    private String createdDateNp;
    private String officeCode;
    private String pisCode;
    private EmployeeMinimalPojo creator;
    @JsonIgnore
    private String historyData;
    private LetterTransferPojo historyDataPojo;
}
