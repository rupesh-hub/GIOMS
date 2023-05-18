package com.gerp.attendance.Proxy.fallback;

import com.gerp.attendance.Pojo.FileConverterPojo;
import com.gerp.attendance.Proxy.MessagingServiceProxy;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class MessagingServiceFallBack extends BaseController implements MessagingServiceProxy {
    private Exception exception;

    public MessagingServiceFallBack injectException(Exception cause ) {
        exception=cause;
        return this;
    }

//    @Override
//    public ResponseEntity<String> getSaruwaLetterTemplate(SaruwaLetterPojo saruwaLetterPojo) {
//         return ResponseEntity.ok(exception.getMessage());
//    }
//
//    @Override
//    public ResponseEntity<String> getSaruwaRequestTemplate(SaruwaRequestPojo saruwaRequestPojo) {
//        return ResponseEntity.ok(exception.getMessage());
//    }
//
//    @Override
//    public ResponseEntity<String> getRawanaTemplate(RawanaTemplate rawanaTemplate) {
//        return ResponseEntity.ok(exception.getMessage());
//    }

    @Override
    public ResponseEntity<byte[]> convertToFile(FileConverterPojo fileConverterPojo) {
        throw new RuntimeException(exception.getMessage());
    }

//    @Override
//    public ResponseEntity<GlobalApiResponse> getAttendance(String from, String to) {
//        return ResponseEntity.ok(getResponse());
//    }

//    private GlobalApiResponse getResponse(){
//        String message = exception.getMessage();
//        String exception = message.substring(0, 34);
//        if (exception.equals("com.netflix.client.ClientException"))
//            message = customMessageSource.get("service.not.available", customMessageSource.get(TemplateServiceProxy.SERVICE_NAME));
//        return errorResponse(message, null);
//    }
}
