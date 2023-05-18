package com.gerp.attendance.Proxy;

import com.gerp.attendance.Pojo.ReportTemplate;
import com.gerp.shared.generic.controllers.BaseController;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
public class TemplateServiceData extends BaseController {

    private final TemplateServiceProxy templateServiceProxy;

    public TemplateServiceData(TemplateServiceProxy templateServiceProxy) {
        this.templateServiceProxy = templateServiceProxy;
    }

    @SneakyThrows
    public String getReportTemplate(ReportTemplate generalTemplate) {
        ResponseEntity<String> responseEntity = templateServiceProxy.generalTemplate(generalTemplate);
        if(responseEntity != null && responseEntity.getBody() != null)
            return responseEntity.getBody();
        else
            return null;
    }

}
