package com.gerp.dartachalani.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentSystemPojo {

    private Integer status;
    private String message;
    private List<SystemDocumentPojo> data;

}
