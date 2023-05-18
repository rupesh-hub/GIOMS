package com.gerp.usermgmt.Proxy.fallback;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.pojo.Mail;
import com.gerp.usermgmt.Proxy.CommunicationServiceProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

@Component
public class CommunicationServiceFallback extends BaseController implements CommunicationServiceProxy {

    private Exception exception;

    public CommunicationServiceFallback injectException(Exception exception){
        this.exception = exception;
        return this;
    }

    @PostMapping("/email/send-message")
    @Override
    public ResponseEntity<?> sendMessage(Mail<?> mail) {
        return ResponseEntity.ok(getResponse());
    }
    public GlobalApiResponse getResponse(){
        String message=exception.getMessage();
        String excep = message.substring(0,34);
        if(excep.equals("com.netflix.client.ClientException"))
            message = customMessageSource.get("service.not.available",customMessageSource.get(CommunicationServiceProxy.SERVICE_NAME));
        return errorResponse(message, null);
    }
}
