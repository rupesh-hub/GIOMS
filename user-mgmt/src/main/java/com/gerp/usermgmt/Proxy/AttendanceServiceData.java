package com.gerp.usermgmt.Proxy;

import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.AttendanceKaajAndLeavePojo;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.MasterDashboardTotalPojo;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.MasterDetailPojo;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.TopTenOfficeDetailPojo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class AttendanceServiceData extends BaseController {
    private final AttendanceProxy attendanceProxy;

    public AttendanceServiceData(AttendanceProxy attendanceProxy) {
        this.attendanceProxy = attendanceProxy;
    }


    @SneakyThrows
    public GlobalApiResponse transferValidate(String pisCode,String officeCode) {
        ResponseEntity<?> responseEntity = attendanceProxy.transferValidate(pisCode,officeCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse != null) {
            return objectMapper.convertValue(globalApiResponse.getData(), GlobalApiResponse.class);
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    public GlobalApiResponse transferEmployeeApprove(String pisCode,String officeCode) {
        ResponseEntity<?> responseEntity = attendanceProxy.transferEmployeeApprove(pisCode,officeCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse != null) {
            return objectMapper.convertValue(globalApiResponse.getData(), GlobalApiResponse.class);
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    public TopTenOfficeDetailPojo filterWithKaajLeave(LocalDate fromDate, LocalDate toDate, Integer limit, Integer offset, Integer by, String type) {
        ResponseEntity<?> responseEntity = attendanceProxy.filterWithLeaveKaaj(fromDate.toString(),toDate.toString(),limit,offset,by, type);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse != null) {
            return objectMapper.convertValue(globalApiResponse.getData(), TopTenOfficeDetailPojo.class);
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    public TopTenOfficeDetailPojo filterWithKaajLeaveExcel(LocalDate fromDate, LocalDate toDate, Integer by, String type) {
        ResponseEntity<?> responseEntity = attendanceProxy.filterWithLeaveKaajExcel(fromDate.toString(),toDate.toString(),by, type);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse != null) {
            return objectMapper.convertValue(globalApiResponse.getData(), TopTenOfficeDetailPojo.class);
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    public MasterDashboardTotalPojo getMasterDashboardTotal(LocalDate fromDate, LocalDate toDate, String officeList) {
        ResponseEntity<?> responseEntity = attendanceProxy.getMasterDashboardTotal(fromDate.toString(),toDate.toString(), officeList);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse != null) {
            return objectMapper.convertValue(globalApiResponse.getData(), MasterDashboardTotalPojo.class);
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    public MasterDetailPojo getOfficeKaajLeave(LocalDate fromDate, LocalDate toDate, List<String> officeCode) {
        ResponseEntity<?> responseEntity = attendanceProxy.getTotalByOfficeCode(new AttendanceKaajAndLeavePojo().builder()
                .fromDate(fromDate.toString())
                .toDate(toDate.toString())
                .officeCode(officeCode)
                .build());

        log.info("request status: "+responseEntity.getStatusCode());

        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse != null) {
            return objectMapper.convertValue(globalApiResponse.getData(), MasterDetailPojo.class);
        }
        else {
            return null;
        }
    }

    @SneakyThrows
    public GlobalApiResponse resetLeave(String pisCode,LocalDate startDate,LocalDate endDate) {
        ResponseEntity<?> responseEntity = attendanceProxy.resetLeave(pisCode, startDate , endDate);
        return objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
    }

}
