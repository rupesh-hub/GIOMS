package com.gerp.dartachalani.dto.document;

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
//    private String decs;
//    private String title;
    private String officeCode;
    private String pisCode;
    private List<String> tags;
    private String tag;
    private String type;
//    private List<String> folderList;
    private String status;
    private String subModuleName;
    private String fiscalYearCode;
    private String designationCode;
    private String sectionCode;


    private Long documentMasterId;
    private List<Long> documentsToDelete;


    private String extraInfo1;
    private String extraInfo2;
    private String extraInfo3;
    private String extraInfo4;
}
