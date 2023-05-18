package com.gerp.attendance.Proxy.fallback;

import com.gerp.attendance.Pojo.document.DocumentMasterResponsePojo;
import com.gerp.attendance.Proxy.DmsService;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

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

    private DocumentMasterResponsePojo getResponse(){
        String message=exception.getMessage();
        String excep = message.substring(0,34);
        if(excep.equals("com.netflix.client.ClientException"))
            message = customMessageSource.get("service.not.available",customMessageSource.get(DmsService.SERVICE_NAME));
        return new DocumentMasterResponsePojo().builder()
                .success(false)
                .message(message)
                .build();
    }
}
