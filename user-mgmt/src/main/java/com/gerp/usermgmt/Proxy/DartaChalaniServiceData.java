package com.gerp.usermgmt.Proxy;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.AttendanceKaajAndLeavePojo;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.MasterDashboardTotalPojo;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.MasterDetailPojo;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.TopTenOfficeDetailPojo;
import com.gerp.usermgmt.pojo.darta.SectionInvolvedPojo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class DartaChalaniServiceData extends BaseController {

    private final DartaChalaniProxy dartaChalaniProxy;

    public DartaChalaniServiceData(DartaChalaniProxy dartaChalaniProxy) {

        this.dartaChalaniProxy = dartaChalaniProxy;
    }


    @SneakyThrows
    public MasterDetailPojo getOfficeDartaCount(LocalDate fromDate, LocalDate toDate, List<String> officeCode) {
        ResponseEntity<?> responseEntity = dartaChalaniProxy.getMasterDashboard(new AttendanceKaajAndLeavePojo().builder()
                .fromDate(fromDate.toString())
                .toDate(toDate.toString())
                .officeCode(officeCode)
                .build());
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse != null) {
            return objectMapper.convertValue(globalApiResponse.getData(), MasterDetailPojo.class);
        } else {
            return new MasterDetailPojo();
        }
    }


    @SneakyThrows
    public TopTenOfficeDetailPojo getMasterDasboardDarta(LocalDate fromDate, LocalDate toDate, Integer limit, Integer pageNo, String officeList, Integer by, String type) {
        ResponseEntity<?> responseEntity = dartaChalaniProxy.getMasterDasboardDarta(fromDate.toString(), toDate.toString(), limit, pageNo, officeList, by, type);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse != null) {
            return objectMapper.convertValue(globalApiResponse.getData(), TopTenOfficeDetailPojo.class);
        } else {
            return new TopTenOfficeDetailPojo();
        }
    }

    @SneakyThrows
    public TopTenOfficeDetailPojo getMasterDasboardDartaExcel(LocalDate fromDate, LocalDate toDate, String officeList, Integer by, String type) {
        ResponseEntity<?> responseEntity = dartaChalaniProxy.getMasterDasboardDartaExcel(fromDate.toString(), toDate.toString(), officeList, by, type);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse != null) {
            return objectMapper.convertValue(globalApiResponse.getData(), TopTenOfficeDetailPojo.class);
        } else {
            return new TopTenOfficeDetailPojo();
        }
    }

    @SneakyThrows
    public MasterDashboardTotalPojo getMasterDasboardDartaTotal(LocalDate fromDate, LocalDate toDate, String officeList) {
        ResponseEntity<?> responseEntity = dartaChalaniProxy.getMasterDasboardDartaTotal(fromDate.toString(), toDate.toString(), officeList);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse != null) {
            return objectMapper.convertValue(globalApiResponse.getData(), MasterDashboardTotalPojo.class);
        } else {
            return new MasterDashboardTotalPojo();
        }
    }

    @SneakyThrows
    public SectionInvolvedPojo checkLetter(String sectoinCode){
        ResponseEntity<?> responseEntity = dartaChalaniProxy.checkLetter(sectoinCode);
        GlobalApiResponse globalApiResponse = objectMapper.convertValue(responseEntity.getBody(), GlobalApiResponse.class);
        if (globalApiResponse != null) {
            return objectMapper.convertValue(globalApiResponse.getData(), SectionInvolvedPojo.class);
        } else {
            return new SectionInvolvedPojo();
        }
    }

}
