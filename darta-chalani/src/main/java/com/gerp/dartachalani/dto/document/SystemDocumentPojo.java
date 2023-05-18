package com.gerp.dartachalani.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemDocumentPojo {

    @JsonProperty("document_master_id")
    private Long documentMasterId;

    @JsonProperty("module_code")
    private String moduleCode;

    @JsonProperty("sub_module_code")
    private String subModuleCode;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("created_at")
    private String createdAt;

    private List<SysDocumentsPojo> documents;
}
