package com.gerp.dartachalani.Proxy.fallback;

import com.gerp.dartachalani.Proxy.CalenderService;
import com.gerp.dartachalani.Proxy.DmsService;
import com.gerp.dartachalani.dto.nepalDate.DateApiResponse;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CalenderServiceFallBack extends BaseController implements CalenderService {
    private Exception exception;
    public CalenderServiceFallBack injectException(Exception cause ) {
        exception=cause;
        return this;
    }
    @Override
    public ResponseEntity<DateApiResponse> getNepaliDateDetails(int month, String year) {
        return ResponseEntity.ok(getResponse());
    }
    private DateApiResponse getResponse() {
        String message = exception.getMessage();
        if (exception.equals("com.netflix.client.ClientException"))
            message = customMessageSource.get("service.not.available", customMessageSource.get(CalenderService.SERVICE_NAME));
        throw new CustomException(message);
    }
}
