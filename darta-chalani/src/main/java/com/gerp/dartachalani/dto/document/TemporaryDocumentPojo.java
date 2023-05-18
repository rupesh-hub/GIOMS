package com.gerp.dartachalani.dto.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@AllArgsConstructor
public class TemporaryDocumentPojo {
    private String fileName;
    private MultiValueMap<String,Object> file;
}
