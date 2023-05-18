package com.gerp.usermgmt.pojo.transfer.document;

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
    private String type;
//    private List<String> folderList;
    private String status;

    private List<Long> documentsToDelete;


    private String extraInfo1;
    private String extraInfo2;
    private String extraInfo3;
    private String extraInfo4;
}
