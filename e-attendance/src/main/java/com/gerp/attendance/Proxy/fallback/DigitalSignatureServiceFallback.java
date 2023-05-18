package com.gerp.attendance.Proxy.fallback;

import com.gerp.attendance.Pojo.DigitalSignatureDto;
import com.gerp.attendance.Proxy.DartaChalaniServiceProxy;
import com.gerp.attendance.Proxy.UserMgmtProxy;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class DigitalSignatureServiceFallback extends BaseController implements DartaChalaniServiceProxy {
    private Exception exception;

    public DigitalSignatureServiceFallback injectException(Exception cause) {
        exception = cause;
        return this;
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getDartaTotal(Timestamp fromDate, Timestamp toDate) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> verifySignature(DigitalSignatureDto digitalSignatureDto) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> generateHasValue(DigitalSignatureDto digitalSignatureDto) {
        return ResponseEntity.ok(getResponse());
    }

    private GlobalApiResponse getResponse() {
        String message = exception.getMessage();
        String exception = message.substring(0, 34);
        if (exception.equals("com.netflix.client.ClientException"))
            message = customMessageSource.get("service.not.available", customMessageSource.get(UserMgmtProxy.SERVICE_NAME));
        return errorResponse(message, null);
    }
}
