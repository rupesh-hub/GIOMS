package com.gerp.attendance.Proxy;

import com.gerp.attendance.Pojo.FileConverterPojo;
import com.gerp.shared.generic.controllers.BaseController;
import lombok.SneakyThrows;
import org.jboss.logging.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class MessagingServiceData extends BaseController {


    private final MessagingServiceProxy messagingServiceProxy;

    public MessagingServiceData(MessagingServiceProxy messagingServiceProxy) {
        this.messagingServiceProxy = messagingServiceProxy;
    }

    @SneakyThrows
    @Cacheable(value = "convertToFile",key = "#fileConverterPojo")
    public  byte[] getFileConverter(FileConverterPojo fileConverterPojo) {
        ResponseEntity< byte[]> responseEntity = messagingServiceProxy.convertToFile(fileConverterPojo);
        if (responseEntity != null && responseEntity.getBody() != null){
            return responseEntity.getBody();
        }
        return null;
    }



}
