package com.gerp.dartachalani.dto.kasamu;

import lombok.Data;

@Data
public class KasamuDocumentPojo {
    private Integer id;
    private Long documentId;
    private Double fileSize;
    private String name;
    private String pisCode;
    private Boolean isMain;
}
