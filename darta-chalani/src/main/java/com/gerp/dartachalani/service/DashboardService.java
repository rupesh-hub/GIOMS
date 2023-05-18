package com.gerp.dartachalani.service;

import com.gerp.dartachalani.dto.DashboardCountPojo;
import com.gerp.dartachalani.dto.MasterDashboardResponsePojo;
import com.gerp.dartachalani.dto.MasterDashboardTotalPojo;
import com.gerp.dartachalani.dto.MasterDataPojo;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    DashboardCountPojo getDashboardCount(String sectionId, String employeeCode, String officeCode, LocalDate startDate, LocalDate endDate, String fiscalYear, String toOfficeCode);

    DashboardCountPojo circleGraphDashboard(String office, String employeeCode, boolean type, String sectionId, String toOfficeCode);

    DashboardCountPojo sidebarCount(String sectionId, String fiscalYearCode);

    MasterDashboardResponsePojo getTotalDartaCount(String fromDate, String toDate,Integer limit, Integer pageNo,String officeList, Integer by, String type);
    MasterDashboardResponsePojo getTotalDartaCountExcel(String fromDate, String toDate,String officeList, Integer by, String type);
    MasterDataPojo getDartaCountByOfficeCode(String fromDate, String toDate,List<String> officeCode);

    MasterDashboardTotalPojo getMasterDashboardTotal(String fromDate, String toDate,String officeList);

}
