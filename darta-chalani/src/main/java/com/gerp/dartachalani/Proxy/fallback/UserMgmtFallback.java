package com.gerp.dartachalani.Proxy.fallback;

import com.gerp.dartachalani.Proxy.UserMgmtProxy;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.pojo.SalutationPojo;
import com.gerp.shared.pojo.json.ApiDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

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
    public ResponseEntity<?> getFiscalActiveYear() {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<?> getFiscalDetailByFiscalYearCode(String fiscalYear) {
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
    public ResponseEntity<GlobalApiResponse> getOfficeDetail(String officeCode) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getOfficeDetailMinimal(String officeCode) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getActiveOfficeStatus(List<String> officeCodes) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getSectionBySectionId(Long sectionId) {
        return ResponseEntity.ok(getResponse());
    }

    @GetMapping("/designation/{designationCode}")
    @Override
    public ResponseEntity<GlobalApiResponse> getDesignationByDesignationCode(String designationCode) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getDesignationByCode(String code) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getPositionByPositionCode(String positionCode) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getServiceGroupByCode(String serviceGroup) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getSectionEmployeeList(Long id) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getDateRangeList(int year, boolean currentDate) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getDateRangeListForYear(int year) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getDelegationDetailsById(Integer id) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getSalutationDetail(List<SalutationPojo> salutationPojos) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getPreviousEmployeeDetail(String pisCode, Long sectionId) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getOfficeTemplate(String officeCode, String type) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getOfficeTemplateById(Long id) {
        return  ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<GlobalApiResponse> getOfficeGroupById(Integer id) {
        return ResponseEntity.ok(getResponse());
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
