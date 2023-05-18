package com.gerp.dartachalani.dto;

import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.Data;

@Data
public class MemoReferenceDoc {
    private Long memoId;
    private Long documentId;
    private Double fileSize;
    private String name;
    private String pisCode;

    private Integer delegatedId;
    private EmployeeMinimalPojo creator;
    private String creatorDesignationNameNp;
    private Boolean isDelegated;
    //this flag is used for additional responsibility
    private Boolean isReassignment;
    private DetailPojo reassignmentSection;
}
