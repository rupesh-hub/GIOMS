package com.gerp.dartachalani.dto.document;

import com.gerp.dartachalani.dto.DetailPojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentPojo {

    private Integer id;
    private Long documentId;
    private Double fileSize;
    private String name;
    private String pisCode;
    private Boolean editable;
    private Boolean isActive;
    private Boolean isMain;
    private Integer delegatedId;
    private EmployeeMinimalPojo creator;
    private String creatorDesignationNameNp;
    private Boolean isDelegated;
    //this flag is used for additional responsibility
    private Boolean isReassignment;
    private DetailPojo reassignmentSection;
}
