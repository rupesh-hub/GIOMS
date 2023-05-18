package com.gerp.dartachalani.Proxy;


import com.gerp.dartachalani.config.StorageProperties;
import com.gerp.dartachalani.dto.document.*;
import com.gerp.dartachalani.dto.nepalDate.DateApiResponse;
import com.gerp.dartachalani.dto.nepalDate.DateDetails;
import com.gerp.dartachalani.dto.nepalDate.Days;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.controllers.BaseController;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class DocumentServiceData extends BaseController {
    private final DmsService dmsService;
    private final CalenderService calenderService;
    private final Path rootLocation;

    public DocumentServiceData(DmsService dmsService, CalenderService calenderService, StorageProperties properties) {
        this.dmsService = dmsService;
        this.calenderService = calenderService;
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

    public void deleteDocuments(MultiValueMap<String, Object> body) {
        ResponseEntity<?> responseEntity = dmsService.deleteDocuments(body);
    }

    public DocumentSystemPojo getDocuments(String pisCode) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("pis_code", pisCode);
        body.add("module_code", "darta_chalani");
//        body.add("sub_module_code", "darta");
        ResponseEntity<DocumentSystemPojo> responseEntity = dmsService.getDocuments(body);
        DocumentSystemPojo documentSystemPojo = responseEntity.getBody();
        if (documentSystemPojo.getStatus() == 1)
            return documentSystemPojo;
        else
            return null;
    }

    public String temporaryDocument(MultiValueMap<String, Object> temporaryDocumentPojo) {
        ResponseEntity<TemporaryDocumentResponsePojo> responseEntity = dmsService.temporaryDocument(temporaryDocumentPojo);
        if (responseEntity.getBody() != null) {
            return responseEntity.getBody().getFile_id();
        }
        return null;
    }
    @SneakyThrows
    @Cacheable(value = "nepaliCalenderDetail")
    public List<List<Days>> getNepaliCalender(int month,String year){
        ResponseEntity<DateApiResponse> responseEntity = calenderService.getNepaliDateDetails(month,year);

       try{
           DateDetails dateDetails = objectMapper.convertValue(responseEntity.getBody().getData(), DateDetails.class);
         return dateDetails.getDays();
       }catch (Exception e){
           throw new CustomException(e.getMessage());
       }
    }
}
