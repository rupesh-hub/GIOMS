package com.gerp.dartachalani.Proxy.fallback;

import com.gerp.dartachalani.Proxy.DmsService;
import com.gerp.dartachalani.Proxy.UserMgmtProxy;
import com.gerp.dartachalani.dto.document.*;
import com.gerp.dartachalani.dto.nepalDate.DateApiResponse;
import com.gerp.dartachalani.dto.nepalDate.DateDetails;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class DocumentServiceFallback extends BaseController implements DmsService {
    private Exception exception;
    public DocumentServiceFallback injectException(Exception cause ) {
        exception=cause;
        return this;
    }


    @Override
    public ResponseEntity<DocumentMasterResponsePojo> create(MultiValueMap<String, Object> docPojo) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<DocumentMasterResponsePojo> update(MultiValueMap<String, Object> docPojo) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<DocumentSystemPojo> getDocuments(MultiValueMap<String, Object> docPojo) {
        return ResponseEntity.ok(getResponse2());
    }

    @Override
    public ResponseEntity<?> deleteDocuments(MultiValueMap<String, Object> docPojo) {
        return null;
    }


    @Override
    public ResponseEntity<TemporaryDocumentResponsePojo> temporaryDocument(MultiValueMap<String, Object> docPojo) {
        return ResponseEntity.ok(getResponse3());
    }



    private DocumentMasterResponsePojo getResponse(){
        String message=exception.getMessage();
        String excep = message.substring(0,10);
        if(excep.equals("com.netflix.client.ClientException"))
            message = customMessageSource.get("service.not.available",customMessageSource.get(DmsService.SERVICE_NAME));
        return new DocumentMasterResponsePojo().builder()
                .success(false)
                .message(message)
                .build();
    }

    private DocumentSystemPojo getResponse2(){
        exception.printStackTrace();
        String message=exception.getMessage();
        String excep = message.substring(0,10);
        if(excep.equals("com.netflix.client.ClientException"))
            message = customMessageSource.get("service.not.available",customMessageSource.get(DmsService.SERVICE_NAME));
        return new DocumentSystemPojo().builder()
                .status(0)
                .message(message)
                .build();
    }

    private TemporaryDocumentResponsePojo getResponse3(){
        exception.printStackTrace();
        String message=exception.getMessage();
        String excep = message.substring(0,10);
        if(excep.equals("com.netflix.client.ClientException"))
            message = customMessageSource.get("service.not.available",customMessageSource.get(DmsService.SERVICE_NAME));

        return new TemporaryDocumentResponsePojo();
    }


}
