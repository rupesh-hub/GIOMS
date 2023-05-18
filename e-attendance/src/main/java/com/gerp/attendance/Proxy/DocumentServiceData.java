package com.gerp.attendance.Proxy;

import com.gerp.attendance.Pojo.document.DocumentMasterResponsePojo;
import com.gerp.attendance.config.StorageProperties;
import com.gerp.shared.generic.controllers.BaseController;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DocumentServiceData extends BaseController {
    private final DmsService dmsService;
    private final Path rootLocation;

    public DocumentServiceData(DmsService dmsService, StorageProperties properties) {
        this.dmsService = dmsService;
        this.rootLocation = Paths.get(properties.getLocation());

    }

    @SneakyThrows
    public DocumentMasterResponsePojo create(MultiValueMap<String, Object> docPojo) {
        final DocumentMasterResponsePojo documentUploadResponsePojo = dmsService.create(docPojo).getBody();
        if (documentUploadResponsePojo.getSuccess())
            return documentUploadResponsePojo;
        else
            return null;
    }

    public DocumentMasterResponsePojo update(MultiValueMap<String, Object> body) {
        ResponseEntity<DocumentMasterResponsePojo> responseEntity = dmsService.update(body);
        System.out.println("checking response"+responseEntity);
        DocumentMasterResponsePojo documentUploadResponsePojo = responseEntity.getBody();
        if (documentUploadResponsePojo.getSuccess())
            return documentUploadResponsePojo;
        else
            return null;
    }
}
