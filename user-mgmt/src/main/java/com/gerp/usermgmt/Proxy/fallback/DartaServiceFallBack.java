package com.gerp.usermgmt.Proxy.fallback;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.usermgmt.Proxy.DartaChalaniProxy;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.AttendanceKaajAndLeavePojo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DartaServiceFallBack extends BaseController implements DartaChalaniProxy {
    private  Exception exception;

    public DartaServiceFallBack injectException(Exception cause) {
        this.exception = cause;
        return this;
    }

    @Override
    public ResponseEntity<?> getMasterDashboard(AttendanceKaajAndLeavePojo attendanceKaajAndLeavePojo) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<?> getMasterDasboardDarta(String fromDate, String toDate, Integer limit, Integer pageNo, String officeList, Integer by, String type) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<?> getMasterDasboardDartaExcel(String fromDate, String toDate, String officeList, Integer by, String type) {
        return ResponseEntity.ok(getResponse());
    }

    @Override
    public ResponseEntity<?> getMasterDasboardDartaTotal(String fromDate, String toDate, String officeList) {
        return ResponseEntity.ok(getResponse());
    }


    private GlobalApiResponse getResponse() {
        String message = exception.getMessage();
        String exception = message.substring(0, 34);
        if (exception.equals("com.netflix.client.ClientException"))
            message = customMessageSource.get("service.not.available", customMessageSource.get(DartaChalaniProxy.SERVICE_NAME));
        return errorResponse(message, null);
    }

    @Override
    public ResponseEntity<?> checkLetter(String sectionCode) {
        return ResponseEntity.ok(getResponse());
    }
}
