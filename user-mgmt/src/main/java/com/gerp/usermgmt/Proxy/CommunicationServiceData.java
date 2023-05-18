package com.gerp.usermgmt.Proxy;

import com.gerp.shared.enums.ResponseStatus;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.pojo.Mail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommunicationServiceData extends BaseController {
    private final CommunicationServiceProxy communicationServiceProxy;

    public CommunicationServiceData(CommunicationServiceProxy communicationServiceProxy) {
        this.communicationServiceProxy = communicationServiceProxy;
    }

    public void sendEmailMessage(Mail<?> mail) {
        ResponseEntity responseEntity = communicationServiceProxy.sendMessage(mail);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            log.info("mail sending success");
        }else{
            throw new RuntimeException(globalApiResponse.getMessage());
        }
    }
}
