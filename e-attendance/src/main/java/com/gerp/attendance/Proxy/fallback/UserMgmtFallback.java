package com.gerp.attendance.Proxy.fallback;

import com.gerp.attendance.Proxy.UserMgmtProxy;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.pojo.json.ApiDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMgmtFallback extends BaseController implements UserMgmtProxy {
    private Exception exception;
    public UserMgmtFallback injectException(Exception cause ) {
        exception=cause;
        return this;
    }

    @Override
    public ResponseEntity<?> getActiveFiscalYearPojo() {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getEmployeeDetail(String pisCode) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getOfficeHead(String officeCode) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getEmployeeDetailMinimal(String pisCode) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getSectionEmployee(Long sectionCode) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getOfficeDetail(String officeCode) {
        return ResponseEntity.ok(getResponse());
    }


    @Override
    public ResponseEntity<GlobalApiResponse> getLowerHierchyPisCode() {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getHierarchyOffice(String officeCode) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getSectionList() {
        return null;
    }

    @Override
    public ResponseEntity<GlobalApiResponse> isUserAuthorized(ApiDetail apiDetail) {
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
