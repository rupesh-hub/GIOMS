package com.gerp.attendance.Pojo.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentMasterResponsePojo {

    private Boolean success;
    @JsonProperty("document_master_id")
    private Long documentMasterId;

    @JsonProperty("document_version_id")
    private Long documentVersionId;

    @JsonProperty("document_size_kb")
    private Double sizeKB;

    @JsonProperty("document_version")
    private List<DocumentResponsePojo> documents;

    private String message;

    @JsonProperty("document_id")
    private Long documentId;
    @JsonProperty("file_name")
    private String fileName;
}
