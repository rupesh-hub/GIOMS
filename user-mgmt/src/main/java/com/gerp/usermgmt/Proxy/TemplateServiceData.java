package com.gerp.usermgmt.Proxy;

import com.gerp.shared.enums.ResponseStatus;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.usermgmt.pojo.transfer.*;
import lombok.SneakyThrows;
import org.jboss.logging.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TemplateServiceData extends BaseController {


    private static final Logger LOG = Logger.getLogger(TemplateServiceProxy.class);
    private final TemplateServiceProxy templateServiceProxy;

    public TemplateServiceData(TemplateServiceProxy templateServiceProxy) {
        this.templateServiceProxy = templateServiceProxy;
    }


    @SneakyThrows
    @Cacheable(value = "sariwaTemplate",key = "#saruwaLetterPojo")
    public String getSaruwaTemplate(SaruwaLetterPojo saruwaLetterPojo) {
        ResponseEntity<String> responseEntity = templateServiceProxy.getSaruwaLetterTemplate(saruwaLetterPojo);
        if (responseEntity != null && responseEntity.getBody() != null){
            return responseEntity.getBody();
        }
        return null;
    }

    @SneakyThrows
    @Cacheable(value = "sariwaTemplate",key = "#saruwaRequestPojo")
    public String getSaruwaRequestTemplate(SaruwaRequestPojo saruwaRequestPojo) {
        ResponseEntity<String> responseEntity = templateServiceProxy.getSaruwaRequestTemplate(saruwaRequestPojo);
        if (responseEntity != null && responseEntity.getBody() != null){
            return responseEntity.getBody();
        }
        return null;
    }

    @SneakyThrows
    @Cacheable(value = "sariwaTemplate",key = "#rawanaTemplate")
    public String getRawanaLetterTemplate(RawanaTemplate rawanaTemplate) {
        ResponseEntity<String> responseEntity = templateServiceProxy.getRawanaTemplate(rawanaTemplate);
        if (responseEntity != null && responseEntity.getBody() != null){
            return responseEntity.getBody();
        }
        return null;
    }

//    @SneakyThrows
//    @Cacheable(value = "convertToFile",key = "#fileConverterPojo")
//    public  byte[] getFileConverter(FileConverterPojo fileConverterPojo) {
//        ResponseEntity< byte[]> responseEntity = templateServiceProxy.convertToFile(fileConverterPojo);
//        if (responseEntity != null && responseEntity.getBody() != null){
//            return responseEntity.getBody();
//        }
//        return null;
//    }

    @SneakyThrows
    @Cacheable(value = "attendance",key = "#from")
    public List<AttendaceDetailPojo> getAttendance(String from, String to) {
        ResponseEntity<GlobalApiResponse> responseEntity = templateServiceProxy.getAttendance(from,to);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse.getStatus() == ResponseStatus.SUCCESS) {
            List<?> objectList = (List<?>) globalApiResponse.getData();
           return objectList.parallelStream().map(ob-> objectMapper.convertValue(ob,AttendaceDetailPojo.class)).collect(Collectors.toList());

        }
        return null;
    }

}
