package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LetterTransferPojo {

    private boolean transferAll;
    private String fromPisCode;
    private EmployeeMinimalPojo fromEmployee;
    private String fromSectionCode;
    private String toPisCode;
    private EmployeeMinimalPojo toEmployee;
    private String toSectionCode;
//    @JsonIgnore
    private List<Long> dartaIds;
//    @JsonIgnore
    private List<Long> chalaniIds;
//    @JsonIgnore
    private List<Long> tippaniIds;

    private List<Long> internalLetterIds;

    private List<String> dartaSubjects;

    private List<String> chalaniSubjects;

    private List<String> tippaniSubjects;

}
