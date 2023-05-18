package com.gerp.usermgmt.pojo.transfer.document;

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
    @JsonProperty("document")
    private List<DocumentResponsePojo> documents;

    private String message;
}
