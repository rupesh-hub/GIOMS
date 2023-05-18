package com.gerp.usermgmt.services.transfer.impl;


import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.config.StorageProperties;
import com.gerp.usermgmt.pojo.transfer.document.DocumentMasterResponsePojo;
import com.gerp.usermgmt.services.transfer.DmsService;
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
