package com.gerp.tms.service;


import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.tms.config.StorageProperties;
import com.gerp.tms.pojo.document.DocumentMasterResponsePojo;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DocumentServiceData extends BaseController {
    private final DmsService dmsService;
    private final Path rootLocation;
    @Autowired
    private StorageProperties properties;

    public DocumentServiceData(DmsService dmsService, StorageProperties properties) {
        this.dmsService = dmsService;
        this.rootLocation = Paths.get(properties.getLocation());

    }

    @SneakyThrows
    public DocumentMasterResponsePojo create(MultiValueMap<String, Object> docPojo) {
        ResponseEntity<DocumentMasterResponsePojo> responseEntity = dmsService.create(docPojo);
        DocumentMasterResponsePojo documentUploadResponsePojo = responseEntity.getBody();
        if (documentUploadResponsePojo.getSuccess())
            return documentUploadResponsePojo;
        else
            return null;
    }

    public DocumentMasterResponsePojo update(MultiValueMap<String, Object> body) {
        ResponseEntity<DocumentMasterResponsePojo> responseEntity = dmsService.update(body);
        DocumentMasterResponsePojo documentUploadResponsePojo = responseEntity.getBody();
        if (documentUploadResponsePojo.getSuccess())
            return documentUploadResponsePojo;
        else
            return null;
    }
}
