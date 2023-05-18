package com.gerp.templating.Proxy.fallback;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.templating.Proxy.EmployeeDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMgmtFallback  extends BaseController implements  EmployeeDetails{

    private Exception exception;
    public UserMgmtFallback injectException(Exception cause ) {
        exception=cause;
        return this;
    }

    private GlobalApiResponse getResponse() {
        String message = exception.getMessage();
        String exception = message.substring(0, 34);
        if (exception.equals("com.netflix.client.ClientException"))
            message = customMessageSource.get("service.not.available", customMessageSource.get(EmployeeDetails.SERVICE_NAME));
        return errorResponse(message, null);
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getEmployeeDetailMinimal(String pisCode) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getOfficeDetail(String pisCode) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getEmployeeListOfLoggedInOffice() {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getActiveFiscalYear() {
        return ResponseEntity.ok(getResponse());
    }
}
