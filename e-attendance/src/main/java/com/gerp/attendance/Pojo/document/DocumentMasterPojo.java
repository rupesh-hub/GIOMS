package com.gerp.attendance.Pojo.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentMasterPojo {

    private Long id;
    private Long documentMasterId;
    private Long documentId;
    private String documentName;
    private Double documentSize;
}
