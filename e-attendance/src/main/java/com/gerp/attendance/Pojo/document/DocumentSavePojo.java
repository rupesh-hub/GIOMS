package com.gerp.attendance.Pojo.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentSavePojo {

    private Long id;
    private String officeCode;
    private String fiscalYearCode;
    private String pisCode;
    private String sectionCode;
    private String designationCode;
    private String moduleKey;
    private String subModuleKey;
    private List<String> tags;
    private String type;
    private String status;
    private Long documentMasterId;
    private List<Long> documentsToDelete;
    private String extraInfo1;
    private String extraInfo2;
    private String extraInfo3;
    private String extraInfo4;

    public DocumentSavePojo(List<Long> documentsToDelete) {
        this.documentsToDelete = documentsToDelete;
    }
}
