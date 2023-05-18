package com.gerp.usermgmt.Proxy.fallback;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.usermgmt.Proxy.AttendanceProxy;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.AttendanceKaajAndLeavePojo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AttendanceServiceFallBack extends BaseController implements AttendanceProxy {
    private  Exception exception;

    public AttendanceServiceFallBack injectException(Exception cause) {
        this.exception = cause;
        return this;
    }

    @Override
    public ResponseEntity<?> transferValidate(String pisCode, String officeCode) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<?> filterWithLeaveKaaj(String fromDate, String toDate, Integer limit, Integer pageNo, Integer by, String type) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<?> filterWithLeaveKaajExcel(String fromDate, String toDate, Integer by, String type) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<?> getMasterDashboardTotal(String fromDate, String toDate, String officeList) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<?> transferEmployeeApprove(String pisCode, String targetOfficeCode) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<?> getTotalByOfficeCode(AttendanceKaajAndLeavePojo attendanceKaajAndLeavePojo) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<?> resetLeave(String pisCode, LocalDate startDate, LocalDate endDate) {
        return ResponseEntity.ok(getResponse());
    }

    private GlobalApiResponse getResponse() {
        String message = exception.getMessage();
        String exception = message.substring(0, 34);
        if (exception.equals("com.netflix.client.ClientException"))
            message = customMessageSource.get("service.not.available", customMessageSource.get(AttendanceProxy.SERVICE_NAME));
        return errorResponse(message, null);
    }
}
