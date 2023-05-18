package com.gerp.attendance.Proxy.fallback;

import com.gerp.attendance.Pojo.ReportTemplate;
import com.gerp.attendance.Proxy.TemplateServiceData;
import com.gerp.attendance.Proxy.TemplateServiceProxy;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TemplateServiceFallback extends BaseController implements TemplateServiceProxy {
    private Exception exception;
    public TemplateServiceFallback injectException(Exception cause ) {
        exception=cause;
        return this;
    }


    private String getResponse() {
        String message = exception.getMessage();
        String exception = message.substring(0, 34);
        if (exception.equals("com.netflix.client.ClientException"))
            message = customMessageSource.get("service.not.available", customMessageSource.get(TemplateServiceProxy.SERVICE_NAME));
        return message;
    }

    @Override
    public ResponseEntity<String> generalTemplate(ReportTemplate generalTemplate) {
        return ResponseEntity.ok(getResponse());
    }
}
