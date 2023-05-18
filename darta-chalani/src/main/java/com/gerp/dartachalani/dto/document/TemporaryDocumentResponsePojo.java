package com.gerp.dartachalani.dto.document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemporaryDocumentResponsePojo {
    private String url;
    private String success;
    private String message;
    private String file_id;
}
