package com.gerp.attendance.Pojo.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentResponsePojo {

    @JsonProperty("document_id")
    private Long id;
    @JsonProperty("document_name")
    private String name;
    @JsonProperty("document_size_kb")
    private Double sizeKB;
}
