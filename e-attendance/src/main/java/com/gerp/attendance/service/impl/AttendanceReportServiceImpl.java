package com.gerp.attendance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.report.AttendanceReportPojo;
import com.gerp.attendance.Pojo.report.AttendanceStatusPojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.AttendanceReportMapper;
import com.gerp.attendance.mapper.LeavePolicyMapper;
import com.gerp.attendance.service.AttendanceReportService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class AttendanceReportServiceImpl implements AttendanceReportService {

    private final AttendanceReportMapper attendanceReportMapper;
    private final UserMgmtServiceData userMgmtServiceData;
    private final LeavePolicyMapper leavePolicyMapper;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    public AttendanceReportServiceImpl(AttendanceReportMapper attendanceReportMapper, UserMgmtServiceData userMgmtServiceData, LeavePolicyMapper leavePolicyMapper) {
        this.attendanceReportMapper = attendanceReportMapper;
        this.userMgmtServiceData = userMgmtServiceData;
        this.leavePolicyMapper = leavePolicyMapper;
    }

    @Override
    public AttendanceMonthlyReportPojoPagination getAttendanceReportMonthly(Date fromDate, Date toDate, Integer page, Integer limit) {
        AttendanceMonthlyReportPojoPagination pagination = new AttendanceMonthlyReportPojoPagination();
        String officeCode = tokenProcessorService.getOfficeCode();
        List<AttendanceMonthlyReportPojo> list = attendanceReportMapper.getMonthlyReport(officeCode, fromDate, toDate);
        if (page != null && limit != null) {
            int sizeToIncrease = (page * limit) + limit;
            pagination.setCurrent(page);
            pagination.setSize(limit);
            pagination.setPages((int) Math.ceil(((double) list.size() / limit)));
            pagination.setTotal(list.size());
            if (list.size() < sizeToIncrease) {
                sizeToIncrease = (page * limit) + (list.size() - (page * limit));
            }
            if (list.size() > (page * limit))
                pagination.setRecords(list.subList(page * limit, sizeToIncrease));
            else
                pagination.setRecords(new ArrayList<>());
            return pagination;
        }
        pagination.setRecords(list);
        return pagination;
    }

    @Override
    public AttendanceAnnualReportMainPojo getAttendanceReportAnnual(Integer filterDate, String pisCode) throws Exception {
        AttendanceAnnualReportMainPojo attendanceAnnualReportMainPojo = new AttendanceAnnualReportMainPojo();
        List<AttendanceAnnualReportMiddlePojo> middleList = new ArrayList<>();
        String pis = pisCode != null ? pisCode : tokenProcessorService.getPisCode();

        EmployeeMinimalPojo employeeMinimalDetailsPojo = userMgmtServiceData.getEmployeeDetailMinimal(pis);
        if (employeeMinimalDetailsPojo.getFunctionalDesignation().getDesignationType().equalsIgnoreCase("SPECIAL_DESIGNATION")) {
            return attendanceAnnualReportMainPojo;
        }
        if (employeeMinimalDetailsPojo != null) {
            attendanceAnnualReportMainPojo.setEmpNameEn(employeeMinimalDetailsPojo.getEmployeeNameEn());
            attendanceAnnualReportMainPojo.setEmpNameNp(employeeMinimalDetailsPojo.getEmployeeNameNp());
            attendanceAnnualReportMainPojo.setFdNameEn(employeeMinimalDetailsPojo.getFunctionalDesignation() != null ? employeeMinimalDetailsPojo.getFunctionalDesignation().getName() : null);
            attendanceAnnualReportMainPojo.setFdNameNp(employeeMinimalDetailsPojo.getFunctionalDesignation() != null ? employeeMinimalDetailsPojo.getFunctionalDesignation().getNameN() : null);
        }

        for (int i = 1; i <= 12; i++) {
            List<AttendanceAnnualReportPojo> list = attendanceReportMapper.getAnnualReport(pis, filterDate, i);
            AttendanceAnnualReportMiddlePojo middlePojo = new AttendanceAnnualReportMiddlePojo();
            middlePojo.setMonth(i + "");
            middlePojo.setMonthlyData(list);
            middleList.add(middlePojo);
        }
        attendanceAnnualReportMainPojo.setMonthlyAttendance(middleList);

        return attendanceAnnualReportMainPojo;
    }

    @Override
    public AttendanceReportPojo getAttendanceReportGeneric(String pisCode, String year) {

        pisCode = pisCode == null ? tokenProcessorService.getPisCode() : "".equalsIgnoreCase(pisCode) ? tokenProcessorService.getPisCode() : pisCode;
        final EmployeeDetailPojo employeeDetail = attendanceReportMapper.getEmployeeDetail(pisCode);
        Map<Integer, List<AttendanceStatusPojo>> monthlyAttendance = new LinkedHashMap();

        String finalPisCode = pisCode;
        String finalYear = year;

        IntStream.range(1, 13).parallel().forEach(i -> {
            monthlyAttendance.put(i, attendanceReportMapper.getAttendanceReport(finalPisCode,
                    tokenProcessorService.getOfficeCode(),
                    false,
                    false,
                    finalYear,
                    i));
        });

        return AttendanceReportPojo
                .builder()
                .monthlyAttendance(monthlyAttendance)
                .nameEn(employeeDetail.getEmployeeNameEn())
                .nameNp(employeeDetail.getEmployeeNameNp())
                .fdNameEn(employeeDetail.getDesignationEn())
                .fdNameNp(employeeDetail.getDesignationNp())
                .build();
    }

    @Override
    public Page<AttendanceReportPojo> getAttendanceReportMonthlyGeneric(final GetRowsRequest paginatedRequest) {

        Page<AttendanceReportPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        String yrs = leavePolicyMapper.currentYear().toString();

        final String year = "".equalsIgnoreCase(paginatedRequest.getYear()) ? yrs : paginatedRequest.getYear() == null ? yrs : paginatedRequest.getYear();

        final String officeCode = tokenProcessorService.getOfficeCode();

        if (tokenProcessorService.isGeneralUser()
                && !tokenProcessorService.getIsOfficeHead()
                && !tokenProcessorService.isAdmin()
                && !tokenProcessorService.isOfficeAdministrator()) {
            if (paginatedRequest.getSearchField() != null) {
                paginatedRequest.getSearchField().put("pisCode", tokenProcessorService.getPisCode());
            } else {
                Map<String, Object> newPisCode = new HashMap<>();
                newPisCode.put("pisCode", tokenProcessorService.getPisCode());
                paginatedRequest.setSearchField(newPisCode);
            }
        }

        page = attendanceReportMapper.monthlyAttendanceReport(
                page,
                officeCode,
                paginatedRequest.getUserStatus(),
                paginatedRequest.getFromDate(),
                paginatedRequest.getToDate(),
                paginatedRequest.getSearchField()
        );

        page.getRecords().parallelStream().forEachOrdered(record -> {
            record.setReport(attendanceReportMapper.getAttendanceReport(record.getOriginalPisCode(),
                    officeCode,
                    record.getIsJoin(),
                    record.getIsLeft(),
//                    paginatedRequest.getYear(),
                    year,
                    Integer.parseInt(paginatedRequest.getMonth())));
        });

        return page;
    }
}
