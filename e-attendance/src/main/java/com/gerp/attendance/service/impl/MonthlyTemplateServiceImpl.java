package com.gerp.attendance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.FileConverterPojo;
import com.gerp.attendance.Pojo.HolidayMapperPojo;
import com.gerp.attendance.Pojo.MonthlyTemplatePojo;
import com.gerp.attendance.Pojo.OfficePojo;
import com.gerp.attendance.Pojo.report.*;
import com.gerp.attendance.Pojo.shift.ShiftDayPojo;
import com.gerp.attendance.Pojo.shift.ShiftPojo;
import com.gerp.attendance.Pojo.shift.ShiftTimePojo;
import com.gerp.attendance.Proxy.MessagingServiceData;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.EmployeeAttendanceMapper;
import com.gerp.attendance.mapper.HolidayMapper;
import com.gerp.attendance.mapper.LeaveRequestMapper;
import com.gerp.attendance.mapper.UserMgmtMapper;
import com.gerp.attendance.repo.LeavePolicyRepo;
import com.gerp.attendance.service.MonthlyTemplateService;
import com.gerp.attendance.service.ShiftService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.utils.WeekNepaliConstants;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
@Slf4j
public class MonthlyTemplateServiceImpl implements MonthlyTemplateService {
    private final TemplateEngine templateEngine;
    private final LeaveRequestMapper leaveRequestMapper;
    private final UserMgmtServiceData userMgmtServiceData;


    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private HolidayMapper holidayMapper;

    @Autowired
    private DateConverter dateConverter;

    @Autowired
    private MessagingServiceData messagingServiceData;

    @Autowired
    private UserMgmtMapper userMgmtMapper;

    @Autowired
    private EmployeeAttendanceMapper employeeAttendanceMapper;

    @Autowired
    private LeavePolicyRepo leavePolicyRepo;

    public MonthlyTemplateServiceImpl(TemplateEngine templateEngine, UserMgmtServiceData userMgmtServiceData,
                                      LeaveRequestMapper leaveRequestMapper) {
        this.templateEngine = templateEngine;
        this.userMgmtServiceData = userMgmtServiceData;
        this.leaveRequestMapper = leaveRequestMapper;
    }

    private MonthlyTemplatePojo monthlyTemplatePojo = new MonthlyTemplatePojo();

    private List<String> topOfficeDetails(String officeCOde, List<String> value, String type, int count) {
        OfficePojo officeDetail = userMgmtServiceData.getOfficeDetail(officeCOde);
        if (officeDetail != null) {
            if (count == 1) {
                value.add(type.equalsIgnoreCase("0") ? officeDetail.getAddressEn() : officeDetail.getAddressNp());
            }
            value.add(type.equalsIgnoreCase("0") ? officeDetail.getNameEn() : officeDetail.getNameNp());

            if (officeDetail.getParentCode() == null || officeDetail.getParentCode().equalsIgnoreCase("8886")) {
                return value;
            }
        } else {
            return value;
        }
        return topOfficeDetails(officeDetail.getParentCode(), value, type, ++count);
    }

    @Override
    public byte[] generateMonthlyPdf(GetRowsRequest paginatedRequest, String lang) {
        paginatedRequest.setLimit(1000);
        Page<EmployeeAttendanceMonthlyReportPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());

        if (tokenProcessorService.isGeneralUser()
                && !tokenProcessorService.getIsOfficeHead() && !tokenProcessorService.isAdmin()
                && !tokenProcessorService.isOfficeAdministrator()) {
            if (paginatedRequest.getSearchField() != null) {
                paginatedRequest.getSearchField().put("pisCode", tokenProcessorService.getPisCode());
            } else {
                Map<String, Object> newPisCode = new HashMap<>();
                newPisCode.put("pisCode", tokenProcessorService.getPisCode());
                paginatedRequest.setSearchField(newPisCode);

            }

        }
        page = employeeAttendanceMapper.filterDataPaginatedMonthly(
                page,
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getFromDate(),
                paginatedRequest.getToDate(),
                paginatedRequest.getSearchField()
        );
        page.getRecords().forEach(x -> {
//            paginatedRequest.setSearchField(new HashMap<>());
            x.setDesignationData(
                    employeeAttendanceMapper.getDesignationData(
                            x.getPisCode(),
                            paginatedRequest.getFromDate(),
                            paginatedRequest.getToDate()
                    )
            );

            //set to true from controller
            if (paginatedRequest.getForDaily()) {
//                paginatedRequest.getSearchField().put("attendanceType",AttendanceStatus.LEAVE);
                x.setMonthlyDailyLogs(
                        employeeAttendanceMapper.getDailyLogData(
                                x.getPisCode(),
                                x.getIsJoin(),
                                x.getIsLeft(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
            }

            //set to true from controller
            if (paginatedRequest.getForLeave()) {
                x.setMonthlyLeaveData(
                        employeeAttendanceMapper.getMonthLeaveData(
                                x.getPisCode(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
            }
            //set to true from controller
            if (paginatedRequest.getForKaaj()) {
                x.setMonthlyKaajData(
                        employeeAttendanceMapper.getMonthKaajData(
                                x.getPisCode(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
            }
            // detailed data with time
            // is set to true in controller
            if (paginatedRequest.getForReportDetail()) {
                x.setMonthlyAttendanceData(
                        employeeAttendanceMapper.getDetailMonthAttendanceData(
                                x.getPisCode(),
                                tokenProcessorService.getOfficeCode(),
                                x.getIsJoin(),
                                x.getIsLeft(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
            }
            //minimal data
            else {
                x.setMonthlyAttendanceData(
                        employeeAttendanceMapper.getMonthAttendanceData(
                                x.getPisCode(),
                                tokenProcessorService.getOfficeCode(),
                                x.getIsJoin(),
                                x.getIsLeft(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
            }
        });

        List<String> headers = new ArrayList<>();
        List<String> headerEmployee = new ArrayList<>();
        int count = 0;
        Context context = new Context();

        if (lang.equalsIgnoreCase("0")) {
            headers.add(0, "NEPAL GOVERNMENT");
            headerEmployee.add(0, "s.n");
            headerEmployee.add(1, "PisCode");
            headerEmployee.add(2, "Employee");
        } else {
            headers.add(0, "नेपाल सरकार");
            headerEmployee.add(0, "क्र.सं.");
            headerEmployee.add(1, "क.संकेत नं.");
            headerEmployee.add(2, "कर्मचारी विवरण");
        }
        count++;

        List<String> topOfficeDetails = topOfficeDetails(tokenProcessorService.getOfficeCode(), new ArrayList<>(), lang, 1);
        for (int i = topOfficeDetails.size() - 1; i >= 0; i--) {
            if (i == topOfficeDetails.size() - 2) {
                headers.add(count, topOfficeDetails.get(i));
            } else {
                headers.add(count, topOfficeDetails.get(i));
            }
            count++;
        }

        if (lang.equalsIgnoreCase("0")) {
            if (paginatedRequest.getSearchField() != null) {
                if (paginatedRequest.getSearchField().get("userType") != null) {
                    String userType = userMgmtMapper.getUserType(1, paginatedRequest.getSearchField());
                    headers.add(count, userMgmtMapper.getDateWiseFiscalYear(1, paginatedRequest.getFromDate(), paginatedRequest.getToDate()).concat("Monthly Report of").concat(StringUtils.capitalize(userType).concat("Employee")));
                } else {
                    headers.add(count, userMgmtMapper.getDateWiseFiscalYear(1, paginatedRequest.getFromDate(), paginatedRequest.getToDate()).concat("Monthly Report of All Employee"));
                }
            } else {
                headers.add(count, userMgmtMapper.getDateWiseFiscalYear(1, paginatedRequest.getFromDate(), paginatedRequest.getToDate()).concat("Monthly Report of All Employee"));
            }
        } else {
            if (paginatedRequest.getSearchField() != null) {
                if (paginatedRequest.getSearchField().get("userType") != null) {
                    String userType = userMgmtMapper.getUserType(0, paginatedRequest.getSearchField());
                    headers.add(count, userMgmtMapper.getDateWiseFiscalYear(0, paginatedRequest.getFromDate(), paginatedRequest.getToDate()).concat("को (").concat(dateConverter.getMonth(paginatedRequest.getMonth())).concat(") को").concat(StringUtils.capitalize(userType).concat(" मासिक उपस्थिति")));
                } else {
                    headers.add(count, userMgmtMapper.getDateWiseFiscalYear(0, paginatedRequest.getFromDate(), paginatedRequest.getToDate()).concat("(").concat(dateConverter.getMonth(paginatedRequest.getMonth())).concat(") को मासिक उपस्थिति"));
                }
            } else {
                headers.add(count, userMgmtMapper.getDateWiseFiscalYear(0, paginatedRequest.getFromDate(), paginatedRequest.getToDate()).concat("(").concat(dateConverter.getMonth(paginatedRequest.getMonth())).concat(") को मासिक उपस्थिति"));
            }
        }
        monthlyTemplatePojo.setHeaders(headers);
        monthlyTemplatePojo.setHeaderEmployee(headerEmployee);
        this.getBodyContain(lang, page, paginatedRequest.getFromDate(), paginatedRequest.getToDate());

        context.setVariable("header", monthlyTemplatePojo);
        FileConverterPojo fileConverterPojo = new FileConverterPojo(templateEngine.process("monthly_detail.html", context));
        return messagingServiceData.getFileConverter(fileConverterPojo);
    }


    private MonthlyTemplatePojo getBodyContain(String lang,
                                               Page<EmployeeAttendanceMonthlyReportPojo> employeeAttendanceMonthlyReportPojoPage,
                                               LocalDate fromDate,
                                               LocalDate toDate) {

        AtomicInteger count = new AtomicInteger(1);
        if (lang.equalsIgnoreCase("0")) {
            LocalDate now = fromDate;
            int countHeader = 0;
            List<String> daysPojos = new ArrayList<>();

            while (now.compareTo(toDate) <= 0) {
                StringBuilder message = new StringBuilder();
                message.append(dateConverter.convertAdToBs(now.toString()).split("-")[2]);
                message.append("<br>");
                message.append(WeekNepaliConstants.getEngVal(now.getDayOfWeek().getValue()));
                daysPojos.add(countHeader, message.toString());
                now = now.plusDays(1);
                countHeader++;

            }

            monthlyTemplatePojo.setMonthDays(daysPojos);

            List<EmployeeAttendanceMonthlyReportPojo> employeeAttendanceMonthlyReportPojoList = new ArrayList<>();
            employeeAttendanceMonthlyReportPojoPage.getRecords().forEach(obj -> {
                List<MonthDataPojo> monthDataPojos = obj.getMonthlyAttendanceData();
                monthDataPojos.sort(Comparator.comparing(MonthDataPojo::getDateEn));
                LocalDate newDate = fromDate;
                Set<String> dateList = new HashSet<>();
                List<String> attendanceDetail = new ArrayList<>();
                EmployeeAttendanceMonthlyReportPojo employeeAttendanceMonthlyReportPojo = new EmployeeAttendanceMonthlyReportPojo();

                for (MonthDataPojo monthDays : monthDataPojos) {
                    dateList.add(monthDays.getDateEn().toString());
                }
                while (newDate.compareTo(toDate) <= 0) {

                    if (dateList.contains(newDate.toString())) {
                        LocalDate newOne = newDate;
                        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();

                        MonthDataPojo monthDataPojo = monthDataPojos.stream().filter(x -> {
                            if (x.getDateEn().compareTo(newOne) == 0) {
                                return true;
                            } else
                                return false;
                        }).collect(Collectors.toList()).get(0);
                        MonthlyDailyLog monthDailyLog = (obj.getMonthlyDailyLogs().stream().filter(y -> y.getDateEn().compareTo(monthDataPojo.getDateEn()) == 0).collect(Collectors.toList()).isEmpty() ? null :
                                obj.getMonthlyDailyLogs().stream().filter(y -> y.getDateEn().compareTo(monthDataPojo.getDateEn()) == 0).collect(Collectors.toList()).get(0));

                        if (monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave")) {
                            MonthDataLeavePojo monthDataLeavePojo = obj.getMonthlyLeaveData().stream().filter(
                                    y -> y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0).collect(Collectors.toList()).isEmpty() ? null :
                                    obj.getMonthlyLeaveData().stream().filter(y -> y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0).collect(Collectors.toList()).get(0);

                            if (monthDataLeavePojo != null) {
                                if (monthDataPojo.getIsHoliday() && leavePolicyRepo.findById(monthDataLeavePojo.getLeavePolicyId()).get().getCountPublicHoliday()) {

                                    if (monthDataLeavePojo.getShortNameNp() != null) {
                                        attendanceDetail.add(monthDataLeavePojo.getShortNameNp());

                                    } else {
                                        attendanceDetail.add(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());
                                    }
                                } else {

                                    ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(obj.getPisCode(), tokenProcessorService.getOfficeCode(), newOne);
                                    if (shiftPojo != null) {
                                        ShiftDayPojo day = shiftPojo.getDays().get(0);
                                        day.getShiftTimes().forEach(z -> {
                                            ShiftTimePojo time = z;
                                            //check for holiday
                                            List<String> parentOfficeCodeWithSelf = new ArrayList<>();
                                            parentOfficeCodeWithSelf.add("00");
                                            String gender = employeeAttendanceMapper.getEmployeeGender(obj.getPisCode(), tokenProcessorService.getOfficeCode());
                                            Long countHoliday = null;
                                            countHoliday = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, newOne, leaveRequestMapper.getNepaliYear(Date.from(newOne.atStartOfDay(ZoneId.systemDefault()).toInstant())), gender);
                                            if (countHoliday != 0) {
                                                try {
                                                    attendanceDetail.add(this.holidayCheckNew(monthDataLeavePojo.getLeavePolicyId(), monthDataLeavePojo.getShortNameNp(), monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort()));
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                            } else if (monthDataLeavePojo != null && monthDataLeavePojo.getShortNameNp() != null) {
                                                attendanceDetail.add(monthDataLeavePojo.getShortNameNp());

                                            } else {

                                                attendanceDetail.add(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());

                                            }
                                        });
                                        if (day.getIsWeekend()) {
                                            try {
                                                if (monthDataLeavePojo != null) {
                                                    attendanceDetail.add(this.holidayCheckNew(monthDataLeavePojo.getLeavePolicyId(), monthDataLeavePojo.getShortNameNp(), monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort()));
                                                } else {
                                                    attendanceDetail.add("सा.बि.");
                                                }

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                    } else {

                                        attendanceDetail.add(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());

                                    }

                                }
                            } else {
                                attendanceDetail.add("अनु");
                            }

                        } else if (monthDailyLog != null) {

                            attendanceDetail.add("लग");


                        } else if (monthDataPojo.getIsHoliday() && !monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave")) {
                            List<String> parentOfficeCodeWithSelf = new ArrayList<>();
                            parentOfficeCodeWithSelf.add("00");
                            HolidayMapperPojo holidayMapperPojo = holidayMapper.holidayDetail(parentOfficeCodeWithSelf, newOne, fiscalYear.getId().toString());
                            if (holidayMapperPojo != null) {

                                attendanceDetail.add(holidayMapperPojo.getShortNameNp() == null ? monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort() : holidayMapperPojo.getShortNameNp());

                            } else if (monthDataPojo.getIsPresent()) {
                                attendanceDetail.add(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());
                            } else if (!monthDataPojo.getIsPresent() && monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("Attendance_on_holiday")) {
                                attendanceDetail.add(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());
                            } else {

                                attendanceDetail.add("सा.बि.");
                            }

                        } else {

                            attendanceDetail.add(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());

                        }

                    } else {
                        attendanceDetail.add("-");
                    }
                    newDate = newDate.plusDays(1);

                }

                employeeAttendanceMonthlyReportPojo.setShortNameNp(attendanceDetail);
                employeeAttendanceMonthlyReportPojo.setEmpNameEn(obj.getEmpNameEn());
                employeeAttendanceMonthlyReportPojo.setEmpNameNp(obj.getEmpNameNp());
                employeeAttendanceMonthlyReportPojo.setFdNameEn(obj.getFdNameEn());
                employeeAttendanceMonthlyReportPojo.setFdNameNp(obj.getFdNameNp());
                employeeAttendanceMonthlyReportPojo.setPisCode(obj.getPisCode());
                employeeAttendanceMonthlyReportPojo.setEmployeeCode(obj.getEmployeeCode());
                employeeAttendanceMonthlyReportPojo.setLanguage(lang);
                employeeAttendanceMonthlyReportPojo.setSn(count.toString());
                DesignationDataPojo designationDataPojo = obj.getDesignationData();
                if (obj.getDesignationData() != null) {
                    designationDataPojo.setEngDay(ordinalSuffix(Integer.parseInt(StringUtils.stripStart(obj.getDesignationData().getDateNp().split("-")[2], "0"))));
                    employeeAttendanceMonthlyReportPojo.setDesignationData(designationDataPojo);
                }
                count.getAndIncrement();
                employeeAttendanceMonthlyReportPojoList.add(employeeAttendanceMonthlyReportPojo);
            });
            monthlyTemplatePojo.setEAttMonthReport(employeeAttendanceMonthlyReportPojoList);
        } else {
            LocalDate now = fromDate;
            int countHeader = 0;
            List<String> daysPojos = new ArrayList<>();

            while (now.compareTo(toDate) <= 0) {
                StringBuilder message = new StringBuilder();
                message.append(dateConverter.convertToNepali(dateConverter.convertAdToBs(now.toString()).split("-")[2]));
                message.append("<br>");
                message.append(WeekNepaliConstants.getNepVal(now.getDayOfWeek().getValue()));
                daysPojos.add(countHeader, message.toString());
                now = now.plusDays(1);
                countHeader++;

            }
            monthlyTemplatePojo.setMonthDays(daysPojos);
            List<EmployeeAttendanceMonthlyReportPojo> employeeAttendanceMonthlyReportPojoList = new ArrayList<>();
            employeeAttendanceMonthlyReportPojoPage.getRecords().forEach(obj -> {
                List<MonthDataPojo> monthDataPojos = obj.getMonthlyAttendanceData();
                monthDataPojos.sort(Comparator.comparing(MonthDataPojo::getDateEn));

                LocalDate newDate = fromDate;
                Set<String> dateList = new HashSet<>();
                List<String> attendanceDetail = new ArrayList<>();
                EmployeeAttendanceMonthlyReportPojo employeeAttendanceMonthlyReportPojo = new EmployeeAttendanceMonthlyReportPojo();
                IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();


                for (MonthDataPojo monthDays : monthDataPojos) {
                    dateList.add(monthDays.getDateEn().toString());
                }
                while (newDate.compareTo(toDate) <= 0) {

                    if (dateList.contains(newDate.toString())) {
                        LocalDate newOne = newDate;
                        MonthDataPojo monthDataPojo = monthDataPojos.stream().filter(x -> {
                            if (x.getDateEn().compareTo(newOne) == 0) {
                                return true;
                            } else
                                return false;
                        }).collect(Collectors.toList()).get(0);
//                        MonthDataPojo monthDataPojo= monthDataPojos.stream().filter(x->x.getDateEn().compareTo(newOne)==0).collect(Collectors.toList()).get(0);
                        MonthlyDailyLog monthDailyLog = (obj.getMonthlyDailyLogs().stream().filter(y -> y.getDateEn().compareTo(monthDataPojo.getDateEn()) == 0).collect(Collectors.toList()).isEmpty() ? null :
                                obj.getMonthlyDailyLogs().stream().filter(y -> y.getDateEn().compareTo(monthDataPojo.getDateEn()) == 0).collect(Collectors.toList()).get(0));
                        if (monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave")) {
                            MonthDataLeavePojo monthDataLeavePojo = obj.getMonthlyLeaveData().stream().filter(y -> y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0).collect(Collectors.toList()).isEmpty() ? null :
                                    obj.getMonthlyLeaveData().stream().filter(y -> y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0).collect(Collectors.toList()).get(0);
//                            if(newOne.compareTo(dateConverter.convertToLocalDateViaInstant(new Date()))<0 && !monthDataPojo.getIsHoliday()){
//                                if (monthDataLeavePojo.getShortNameNp() != null) {
//                                    html.append("<td>")
//                                            .append(monthDataLeavePojo.getShortNameNp())
//                                            .append("</td>");
//                                }else {
//                                    html.append("<td>")
//                                            .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort())
//                                            .append("</td>");
//                                }
//                            }
                            if (monthDataPojo.getIsHoliday() && leavePolicyRepo.findById(monthDataLeavePojo.getLeavePolicyId()).get().getCountPublicHoliday()) {

                                if (monthDataLeavePojo.getShortNameNp() != null) {

                                    attendanceDetail.add(monthDataLeavePojo.getShortNameNp());

                                } else {

                                    attendanceDetail.add(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());

                                }
                            } else {

                                ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(obj.getPisCode(), tokenProcessorService.getOfficeCode(), newOne);
                                if (shiftPojo != null) {
                                    ShiftDayPojo day = shiftPojo.getDays().get(0);
                                    day.getShiftTimes().forEach(z -> {
                                        ShiftTimePojo time = z;
                                        //check for holiday
                                        List<String> parentOfficeCodeWithSelf = new ArrayList<>();
                                        parentOfficeCodeWithSelf.add("00");
                                        String gender = employeeAttendanceMapper.getEmployeeGender(obj.getPisCode(), tokenProcessorService.getOfficeCode());
                                        Long countHoliday = null;
                                        countHoliday = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, newOne, leaveRequestMapper.getNepaliYear(Date.from(newOne.atStartOfDay(ZoneId.systemDefault()).toInstant())), gender);
                                        if (countHoliday != 0) {
                                            try {
                                                attendanceDetail.add(this.holidayCheckNew(monthDataLeavePojo.getLeavePolicyId(), monthDataLeavePojo.getShortNameNp(), monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort()));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        } else if (monthDataLeavePojo != null && monthDataLeavePojo.getShortNameNp() != null) {
                                            attendanceDetail.add(monthDataLeavePojo.getShortNameNp());

                                        } else {
                                            attendanceDetail.add(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());

                                        }
                                    });
                                    if (day.getIsWeekend()) {
                                        try {
                                            log.info("errorOne" + obj.getPisCode());
                                            if (monthDataLeavePojo != null) {
                                                attendanceDetail.add(this.holidayCheckNew(monthDataLeavePojo.getLeavePolicyId(), monthDataLeavePojo.getShortNameNp(), monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort()));
                                            } else {
                                                attendanceDetail.add("अनु");
                                            }

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                } else {

                                    attendanceDetail.add(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());

                                }

                            }

                        } else if (monthDailyLog != null) {
                            attendanceDetail.add("लग");


                        } else if (monthDataPojo.getIsHoliday() && !monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave")) {
                            List<String> parentOfficeCodeWithSelf = new ArrayList<>();
                            parentOfficeCodeWithSelf.add("00");
                            HolidayMapperPojo holidayMapperPojo = holidayMapper.holidayDetail(parentOfficeCodeWithSelf, newOne, fiscalYear.getId().toString());
                            if (holidayMapperPojo != null) {

                                attendanceDetail.add(holidayMapperPojo.getShortNameNp() == null ? monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort() : holidayMapperPojo.getShortNameNp());

                            } else if (monthDataPojo.getIsPresent()) {
                                attendanceDetail.add(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());
                            } else if (!monthDataPojo.getIsPresent() && monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("Attendance_on_holiday")) {
                                attendanceDetail.add(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());
                            } else {

                                attendanceDetail.add("सा.बि.");

                            }

                        } else {
                            attendanceDetail.add(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());

                        }

                    } else {
//                        attendanceDetail.add(" ");
                        attendanceDetail.add("-");
//                        attendanceDetail.add("अनु");

                    }
                    newDate = newDate.plusDays(1);

                }


                employeeAttendanceMonthlyReportPojo.setShortNameNp(attendanceDetail);
                employeeAttendanceMonthlyReportPojo.setEmpNameEn(obj.getEmpNameEn());
                employeeAttendanceMonthlyReportPojo.setEmpNameNp(obj.getEmpNameNp());
                employeeAttendanceMonthlyReportPojo.setFdNameEn(obj.getFdNameEn());
                employeeAttendanceMonthlyReportPojo.setFdNameNp(obj.getFdNameNp());
                employeeAttendanceMonthlyReportPojo.setPisCode(dateConverter.convertToNepali(obj.getPisCode()));
                employeeAttendanceMonthlyReportPojo.setEmployeeCode(dateConverter.convertToNepali(obj.getEmployeeCode()));
                employeeAttendanceMonthlyReportPojo.setSn(dateConverter.convertToNepali(count.toString()));
                employeeAttendanceMonthlyReportPojo.setLanguage(lang);
                /**
                 * nd day
                 */
                if (obj.getDesignationData() != null) {
                    DesignationDataPojo designationDataPojo = obj.getDesignationData();
                    designationDataPojo.setNpDay(StringUtils.stripStart(obj.getDesignationData().getDateNp().split("-")[2], "0"));
                    employeeAttendanceMonthlyReportPojo.setDesignationData(designationDataPojo);
                }

                count.getAndIncrement();
                employeeAttendanceMonthlyReportPojoList.add(employeeAttendanceMonthlyReportPojo);

            });
            monthlyTemplatePojo.setEAttMonthReport(employeeAttendanceMonthlyReportPojoList);

        }


        return monthlyTemplatePojo;
    }

    public String ordinalSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        switch (day % 10) {
            case 1:
                return day + "st";
            case 2:
                return day + "nd";
            case 3:
                return day + "rd";
            default:
                return day + "th";
        }

//        int j = i % 10,
//
//                k = i % 100;
//
//        if (j == 1 && k != 11) {
//
//            return i + "st";
//
//        }
//
//        if (j == 2 && k != 12) {
//
//            return i + "nd";
//
//        }
//
//        if (j == 3 && k != 13) {
//
//            return i + "rd";
//
//        }
//
//        return i + "th";

    }

    @Override
    public byte[] generateMonthlyDetailPdf(GetRowsRequest paginatedRequest, String lang) {
        paginatedRequest.setLimit(1000);
        Page<EmployeeAttendanceMonthlyReportPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());
        page = employeeAttendanceMapper.filterDataPaginatedMonthly(
                page,
                paginatedRequest.getOfficeCode(),
                paginatedRequest.getUserStatus(),
                paginatedRequest.getFromDate(),
                paginatedRequest.getToDate(),
                paginatedRequest.getSearchField()
        );
        page.getRecords().forEach(x -> {
//            paginatedRequest.setSearchField(new HashMap<>());

            //set to true from controller
            if (paginatedRequest.getForLeave()) {
//                paginatedRequest.getSearchField().put("attendanceType",AttendanceStatus.LEAVE);
                x.setMonthlyLeaveData(
                        employeeAttendanceMapper.getMonthLeaveData(
                                x.getPisCode(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
            }

            if (paginatedRequest.getForDaily()) {
//                paginatedRequest.getSearchField().put("attendanceType",AttendanceStatus.LEAVE);
                x.setMonthlyDailyLogs(
                        employeeAttendanceMapper.getDailyLogData(
                                x.getPisCode(),
                                x.getIsJoin(),
                                x.getIsLeft(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
            }
            //set to true from controller
            if (paginatedRequest.getForKaaj()) {
//                paginatedRequest.getSearchField().put("attendanceType",AttendanceStatus.KAAJ);
                x.setMonthlyKaajData(
                        employeeAttendanceMapper.getMonthKaajData(
                                x.getPisCode(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
            }
            // detailed data with time
            // is set to true in controller
            if (paginatedRequest.getForReportDetail()) {
                x.setMonthlyAttendanceData(
                        employeeAttendanceMapper.getDetailMonthAttendanceData(
                                x.getPisCode(),
                                tokenProcessorService.getOfficeCode(),
                                x.getIsJoin(),
                                x.getIsLeft(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
            }
            //minimal data
            else {
                x.setMonthlyAttendanceData(
                        employeeAttendanceMapper.getMonthAttendanceData(
                                x.getPisCode(),
                                tokenProcessorService.getOfficeCode(),
                                x.getIsJoin(),
                                x.getIsLeft(),
                                paginatedRequest.getFromDate(),
                                paginatedRequest.getToDate()
                        )
                );
            }
        });

        StringBuilder html;
        StringBuilder lateRemarks;
        try {
            html = getForMonthlyDetail(lang, page, paginatedRequest.getFromDate(), paginatedRequest.getToDate()).get(0);
            lateRemarks = getForMonthlyDetail(lang, page, paginatedRequest.getFromDate(), paginatedRequest.getToDate()).get(1);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }

        Context context = new Context();
        StringBuilder heading = new StringBuilder();
        if (lang.equalsIgnoreCase("0")) {
            heading.append("NEPAL GOVERNMENT <br>");
        } else {
            heading.append("नेपाल सरकार <br>");
        }
        List<String> topOfficeDetails = topOfficeDetails(tokenProcessorService.getOfficeCode(), new ArrayList<>(), lang, 1);
        for (int i = topOfficeDetails.size() - 1; i >= 0; i--) {
            if (i == topOfficeDetails.size() - 2) {
                heading.append("<span style=\"font-size:20px\">").append(topOfficeDetails.get(i)).append("</span>");
            } else {
                heading.append(topOfficeDetails.get(i));
            }
            heading.append("<br>");

        }
        heading.append("<br>");
        if (lang.equalsIgnoreCase("0")) {
            if (paginatedRequest.getSearchField().get("userType") != null) {
                String userType = userMgmtMapper.getUserType(1, paginatedRequest.getSearchField());
                heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getDateWiseFiscalYear(1, paginatedRequest.getFromDate(), paginatedRequest.getToDate())).append("Monthly Detail Report of").append(StringUtils.capitalize(userType)).append("Employee").append("</span>");
            } else {

                heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getDateWiseFiscalYear(1, paginatedRequest.getFromDate(), paginatedRequest.getToDate())).append("Monthly Detail Report of All Employee").append("</span>");

            }
        } else {
            if (paginatedRequest.getSearchField().get("userType") != null) {
                String userType = userMgmtMapper.getUserType(0, paginatedRequest.getSearchField());
                heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getDateWiseFiscalYear(0, paginatedRequest.getFromDate(), paginatedRequest.getToDate())).append("(").append(dateConverter.getMonth(paginatedRequest.getMonth())).append(")").append(StringUtils.capitalize(userType)).append("को मासिक उपस्थिति जानकारी").append("</span>");
            } else {

                heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getDateWiseFiscalYear(0, paginatedRequest.getFromDate(), paginatedRequest.getToDate())).append("(").append(dateConverter.getMonth(paginatedRequest.getMonth())).append(") को मासिक उपस्थिति जानकारी").append("</span>");

            }

        }
//        heading.append(topOfficeDetails.get(topOfficeDetails.size()-1));
        context.setVariable("header", heading.toString());
        context.setVariable("body", html);
        StringBuilder remarks = new StringBuilder();
        ;
        remarks.append("<span style=\"font-size:20px\">")
                .append("विवरण:")
                .append("</span>");


        if (lateRemarks != null) {
            context.setVariable("remarksTitle", remarks);
        }
        context.setVariable("remarks", lateRemarks);
        String process = templateEngine.process("header.html", context);
        FileConverterPojo fileConverterPojo = new FileConverterPojo(process);
        return messagingServiceData.getFileConverter(fileConverterPojo);
    }

    private List<StringBuilder> getForMonthlyDetail(String lang, Page<EmployeeAttendanceMonthlyReportPojo> employeeAttendanceMonthlyReportPojoPage, LocalDate fromDate, LocalDate toDate) {
        List<StringBuilder> htmlString = new ArrayList<>();

        StringBuilder html = new StringBuilder();
        StringBuilder lateRemarks = new StringBuilder();
        AtomicInteger count = new AtomicInteger(1);
        if (lang.equalsIgnoreCase("0")) {
            html.append("<tr>" +
                    "<th>Pis Code</th>" +
                    "<th>Employee Name</th>");

            LocalDate now = fromDate;
            int countHeader = 0;
            while (now.compareTo(toDate) <= 0) {

                html.append("<th>").append(dateConverter.convertAdToBs(now.toString()).split("-")[2])
                        .append("<br>").append(WeekNepaliConstants.getEngVal(now.getDayOfWeek().getValue()))
                        .append("</th>");
//                html.append("<th>").append(WeekNepaliConstants.getEngVal(now.getDayOfWeek().getValue())).append("</th>");
                now = now.plusDays(1);
                countHeader++;

            }
            html.append("</tr>");


            employeeAttendanceMonthlyReportPojoPage.getRecords().forEach(obj -> {
                List<MonthDataPojo> monthDataPojos = obj.getMonthlyAttendanceData();
                monthDataPojos.sort(Comparator.comparing(MonthDataPojo::getDateEn));
                html.append("<tr>")
                        .append("<td>").append(obj.getPisCode()).append("</td>")
                        .append("<td>").append(StringUtils.capitalize(obj.getEmpNameEn()))
                        .append("(" + StringUtils.capitalize(obj.getFdNameEn()) + ")").append("</td>");
                LocalDate newDate = fromDate;
                Set<String> dateList = new HashSet<>();
                int countRemarks = 0;
                for (MonthDataPojo monthDays : monthDataPojos) {
                    dateList.add(monthDays.getDateEn().toString());
                }
                while (newDate.compareTo(toDate) <= 0) {

                    if (dateList.contains(newDate.toString())) {
                        LocalDate newOne = newDate;
                        MonthDataPojo monthDataPojo = monthDataPojos.stream().filter(x -> {
                            if (x.getDateEn().compareTo(newOne) == 0) {
                                return true;
                            } else
                                return false;
                        }).collect(Collectors.toList()).isEmpty() ? null : monthDataPojos.stream().filter(x -> {
                            if (x.getDateEn().compareTo(newOne) == 0) {
                                return true;
                            } else
                                return false;
                        }).collect(Collectors.toList()).get(0);

                        if (monthDataPojo != null) {
                            if (monthDataPojo.getCheckin() != null && monthDataPojo.getShiftCheckin() != null) {
                                if (monthDataPojo.getCheckin().compareTo(monthDataPojo.getShiftCheckin()) > 0) {
                                    try {
                                        html.append("<td>")
                                                .append("<div>")
                                                .append(monthDataPojo.getCheckin() == null ? "" : (dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckin().toString()))))
                                                .append("</div>")
                                                .append("<div>")
                                                .append(monthDataPojo.getCheckout() == null ? "" : dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckout().toString())))
                                                .append("*")
                                                .append("</div>")
                                                .append("</td>");
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
//                                if(countRemarks<=0){
//                                    lateRemarks.append("<span style=\"font-size:20px\">")
//                                            .append("Remarks :")
//                                            .append("</span>")
//                                            .append("<br>");
//                                }
                                    lateRemarks.append("<span style=\"font-size:20px\">")
                                            .append(StringUtils.capitalize(obj.getEmpNameNp()))
                                            .append(": (" + dateConverter.convertBSToDevnagari(monthDataPojo.getDateNp()) + ")")
                                            .append(":")
                                            .append(monthDataPojo.getLateRemarks() == null ? "विवरण पेश गरिएको छैन ।" : monthDataPojo.getLateRemarks())
                                            .append("</span>")
                                            .append("<br>");
                                    countRemarks++;
                                } else {
                                    try {
                                        html.append("<td>")
                                                .append("<div>")
                                                .append(monthDataPojo.getCheckin() == null ? "" : (dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckin().toString()))))
                                                .append("</div>")
                                                .append("<div>")
                                                .append(monthDataPojo.getCheckout() == null ? "" : dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckout().toString())))
                                                .append("</div>")
                                                .append("</td>");
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                MonthlyDailyLog monthDailyLog = (obj.getMonthlyDailyLogs().stream().filter(y -> y.getDateEn().compareTo(monthDataPojo.getDateEn()) == 0).collect(Collectors.toList()).isEmpty() ? null :
                                        obj.getMonthlyDailyLogs().stream().filter(y -> y.getDateEn().compareTo(monthDataPojo.getDateEn()) == 0).collect(Collectors.toList()).get(0));
                                IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();

//                            if(monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave")){
//                                MonthDataLeavePojo monthDataLeavePojo=obj.getMonthlyLeaveData().stream().filter(y-> y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0).collect(Collectors.toList()).get(0);
//                              MonthDataLeavePojo monthDataLeavePojo = obj.getMonthlyLeaveData().stream().filter(y -> {
//                                  if (y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0) {
//                                      return true;
//                                  } else {
//                                      return false;
//                                  }
//                              }).collect(Collectors.toList()).isEmpty()?null:obj.getMonthlyLeaveData().stream().filter(y -> {
//                                  if (y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0) {
//                                      return true;
//                                  } else {
//                                      return false;
//                                  }
//                              }).collect(Collectors.toList()).get(0);
//                              if (monthDataPojo != null) {

                                if (monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave")) {
                                    MonthDataLeavePojo monthDataLeavePojo = obj.getMonthlyLeaveData().stream().filter(y -> y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0).collect(Collectors.toList()).get(0);
//                            if(newOne.compareTo(dateConverter.convertToLocalDateViaInstant(new Date()))<0 && !monthDataPojo.getIsHoliday()){
//                                if (monthDataLeavePojo.getShortNameNp() != null) {
//                                    html.append("<td>")
//                                            .append(monthDataLeavePojo.getShortNameNp())
//                                            .append("</td>");
//                                }else {
//                                    html.append("<td>")
//                                            .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort())
//                                            .append("</td>");
//                                }
//                            }

                                    if (monthDataPojo.getIsHoliday() && leavePolicyRepo.findById(monthDataLeavePojo.getLeavePolicyId()).get().getCountPublicHoliday()) {

                                        if (monthDataLeavePojo.getShortNameNp() != null) {
                                            html.append("<td>")
                                                    .append(monthDataLeavePojo.getShortNameNp())
                                                    .append("</td>");
                                        } else {
                                            html.append("<td>")
                                                    .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort())
                                                    .append("</td>");
                                        }
                                    } else {

                                        ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(obj.getPisCode(), tokenProcessorService.getOfficeCode(), newOne);
                                        if (shiftPojo != null) {
                                            ShiftDayPojo day = shiftPojo.getDays().get(0);
                                            day.getShiftTimes().forEach(z -> {
                                                ShiftTimePojo time = z;
                                                //check for holiday
                                                List<String> parentOfficeCodeWithSelf = new ArrayList<>();
                                                parentOfficeCodeWithSelf.add("00");
                                                String gender = employeeAttendanceMapper.getEmployeeGender(obj.getPisCode(), tokenProcessorService.getOfficeCode());
                                                Long countHoliday = null;
                                                countHoliday = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, newOne, leaveRequestMapper.getNepaliYear(Date.from(newOne.atStartOfDay(ZoneId.systemDefault()).toInstant())), gender);
                                                if (countHoliday != 0) {
                                                    try {
                                                        html.append(this.holidayCheck(monthDataLeavePojo.getLeavePolicyId(), monthDataLeavePojo.getShortNameNp(), monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort()));
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }

                                                } else if (monthDataLeavePojo.getShortNameNp() != null) {
                                                    html.append("<td>")
                                                            .append(monthDataLeavePojo.getShortNameNp())
                                                            .append("</td>");
                                                } else {
                                                    html.append("<td>")
                                                            .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort())
                                                            .append("</td>");
                                                }
                                            });
                                            if (day.getIsWeekend()) {
                                                try {

                                                    html.append(this.holidayCheck(monthDataLeavePojo.getLeavePolicyId(), monthDataLeavePojo.getShortNameNp(), monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort()));
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                        } else {
                                            html.append("<td>")
                                                    .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort())
                                                    .append("</td>");
                                        }

                                    }

                                } else if (monthDailyLog != null) {
                                    html.append("<td>")
                                            .append("लग")
                                            .append("</td>");

                                } else if (monthDataPojo.getIsHoliday() && !monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave")) {
                                    List<String> parentOfficeCodeWithSelf = new ArrayList<>();
                                    parentOfficeCodeWithSelf.add("00");
                                    HolidayMapperPojo holidayMapperPojo = holidayMapper.holidayDetail(parentOfficeCodeWithSelf, newOne, fiscalYear.getId().toString());
                                    if (holidayMapperPojo != null) {
                                        html.append("<td>")
                                                .append(holidayMapperPojo.getShortNameNp() == null ? monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort() : holidayMapperPojo.getShortNameNp())
                                                .append("</td>");
                                    } else {
                                        html.append("<td>")
                                                .append("सा.बि.")
                                                .append("</td>");
                                    }

                                } else if (monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave") && monthDataPojo.getIsPresent()) {
                                    html.append("<td>")
                                            .append("उ")
                                            .append("</td>");

                                } else {
                                    html.append("<td>")
                                            .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort())
                                            .append("</td>");
                                }
                            }

                        }


                    } else {
                        html.append("<td>").append(" ")
                                .append("</td>");
                    }
                    newDate = newDate.plusDays(1);

                }
                html.append("</tr>");
                count.getAndIncrement();

            });
            html.append("</tbody>");
        } else {

            html.append("<tr>" +
                    "<th>क.सं.नं.</th>" +
                    "<th>कर्मचारी विवरण</th>");
            LocalDate now = fromDate;
            int countHeader = 0;
            while (now.compareTo(toDate) <= 0) {
                html.append("<th>").append(dateConverter.convertToNepali(dateConverter.convertAdToBs(now.toString()).split("-")[2]))
                        .append("<br>").append(WeekNepaliConstants.getNepVal(now.getDayOfWeek().getValue()))
                        .append("</th>");
//                html.append("<th>").append().append("</th>");
                now = now.plusDays(1);
                countHeader++;

            }
            html.append("</tr>");


            employeeAttendanceMonthlyReportPojoPage.getRecords().forEach(obj -> {
                List<MonthDataPojo> monthDataPojos = obj.getMonthlyAttendanceData();
                monthDataPojos.sort(Comparator.comparing(MonthDataPojo::getDateEn));
                html.append("<tr>")
                        .append("<td>").append(dateConverter.convertToNepali(obj.getPisCode())).append("</td>")
                        .append("<td>").append(StringUtils.capitalize(obj.getEmpNameNp()))
                        .append("(" + StringUtils.capitalize(obj.getFdNameNp() == null ? "-" : obj.getFdNameNp()) + ")").append("</td>");
                LocalDate newDate = fromDate;
                Set<String> dateList = new HashSet<>();

                for (MonthDataPojo monthDays : monthDataPojos) {
                    dateList.add(monthDays.getDateEn().toString());
                }
                int countRemarks = 0;
                while (newDate.compareTo(toDate) <= 0) {

                    if (dateList.contains(newDate.toString())) {
                        LocalDate newOne = newDate;
                        MonthDataPojo monthDataPojo = monthDataPojos.stream().filter(x -> {
                            if (x.getDateEn().compareTo(newOne) == 0) {
                                return true;
                            } else
                                return false;
                        }).collect(Collectors.toList()).isEmpty() ? null : monthDataPojos.stream().filter(x -> {
                            if (x.getDateEn().compareTo(newOne) == 0) {
                                return true;
                            } else
                                return false;
                        }).collect(Collectors.toList()).get(0);
                        if (monthDataPojo != null) {
                            if (monthDataPojo.getCheckin() != null && monthDataPojo.getShiftCheckin() != null) {
                                if (monthDataPojo.getCheckin().compareTo(monthDataPojo.getShiftCheckin()) > 0) {
                                    try {
                                        html.append("<td>")
                                                .append("<div>")
                                                .append(monthDataPojo.getCheckin() == null ? "" : (dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckin().toString()))))
                                                .append("</div>")
                                                .append("<div>")
                                                .append(monthDataPojo.getCheckout() == null ? "" : dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckout().toString())))
                                                .append("*")
                                                .append("</div>")
                                                .append("</td>");
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
//                                if(countRemarks<=0){
//                                    lateRemarks.append("<span style=\"font-size:20px\">")
//                                            .append("Remarks :")
//                                            .append("</span>")
//                                            .append("<br>");
//                                }
                                    lateRemarks.append("<span style=\"font-size:20px\">")
                                            .append(StringUtils.capitalize(obj.getEmpNameNp()))
                                            .append(": (" + dateConverter.convertBSToDevnagari(monthDataPojo.getDateNp()) + ")")
                                            .append(":")
                                            .append(monthDataPojo.getLateRemarks() == null ? "विवरण पेश गरिएको छैन ।" : monthDataPojo.getLateRemarks())
                                            .append("</span>")
                                            .append("<br>");
                                    countRemarks++;
                                } else {
                                    try {
                                        html.append("<td>")
                                                .append("<div>")
                                                .append(monthDataPojo.getCheckin() == null ? "" : (dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckin().toString()))))
                                                .append("</div>")
                                                .append("<div>")
                                                .append(monthDataPojo.getCheckout() == null ? "" : dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckout().toString())))
                                                .append("</div>")
                                                .append("</td>");
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                MonthlyDailyLog monthDailyLog = (obj.getMonthlyDailyLogs().stream().filter(y -> y.getDateEn().compareTo(monthDataPojo.getDateEn()) == 0).collect(Collectors.toList()).isEmpty() ? null :
                                        obj.getMonthlyDailyLogs().stream().filter(y -> y.getDateEn().compareTo(monthDataPojo.getDateEn()) == 0).collect(Collectors.toList()).get(0));
                                IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();

//                            if(monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave")){
//                                MonthDataLeavePojo monthDataLeavePojo=obj.getMonthlyLeaveData().stream().filter(y-> y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0).collect(Collectors.toList()).get(0);
//                              MonthDataLeavePojo monthDataLeavePojo = obj.getMonthlyLeaveData().stream().filter(y -> {
//                                  if (y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0) {
//                                      return true;
//                                  } else {
//                                      return false;
//                                  }
//                              }).collect(Collectors.toList()).isEmpty()?null:obj.getMonthlyLeaveData().stream().filter(y -> {
//                                  if (y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0) {
//                                      return true;
//                                  } else {
//                                      return false;
//                                  }
//                              }).collect(Collectors.toList()).get(0);
//                              if (monthDataPojo != null) {

                                if (monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave")) {
                                    MonthDataLeavePojo monthDataLeavePojo = obj.getMonthlyLeaveData().stream().filter(y -> y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0).collect(Collectors.toList()).get(0);
//                            if(newOne.compareTo(dateConverter.convertToLocalDateViaInstant(new Date()))<0 && !monthDataPojo.getIsHoliday()){
//                                if (monthDataLeavePojo.getShortNameNp() != null) {
//                                    html.append("<td>")
//                                            .append(monthDataLeavePojo.getShortNameNp())
//                                            .append("</td>");
//                                }else {
//                                    html.append("<td>")
//                                            .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort())
//                                            .append("</td>");
//                                }
//                            }

                                    if (monthDataPojo.getIsHoliday() && leavePolicyRepo.findById(monthDataLeavePojo.getLeavePolicyId()).get().getCountPublicHoliday()) {

                                        if (monthDataLeavePojo.getShortNameNp() != null) {
                                            html.append("<td>")
                                                    .append(monthDataLeavePojo.getShortNameNp())
                                                    .append("</td>");
                                        } else {
                                            html.append("<td>")
                                                    .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort())
                                                    .append("</td>");
                                        }
                                    } else {

                                        ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(obj.getPisCode(), tokenProcessorService.getOfficeCode(), newOne);
                                        if (shiftPojo != null) {
                                            ShiftDayPojo day = shiftPojo.getDays().get(0);
                                            day.getShiftTimes().forEach(z -> {
                                                ShiftTimePojo time = z;
                                                //check for holiday
                                                List<String> parentOfficeCodeWithSelf = new ArrayList<>();
                                                parentOfficeCodeWithSelf.add("00");
                                                String gender = employeeAttendanceMapper.getEmployeeGender(obj.getPisCode(), tokenProcessorService.getOfficeCode());
                                                Long countHoliday = null;
                                                countHoliday = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, newOne, leaveRequestMapper.getNepaliYear(Date.from(newOne.atStartOfDay(ZoneId.systemDefault()).toInstant())), gender);
                                                if (countHoliday != 0) {
                                                    try {
                                                        html.append(this.holidayCheck(monthDataLeavePojo.getLeavePolicyId(), monthDataLeavePojo.getShortNameNp(), monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort()));
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }

                                                } else if (monthDataLeavePojo.getShortNameNp() != null) {
                                                    html.append("<td>")
                                                            .append(monthDataLeavePojo.getShortNameNp())
                                                            .append("</td>");
                                                } else {
                                                    html.append("<td>")
                                                            .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort())
                                                            .append("</td>");
                                                }
                                            });
                                            if (day.getIsWeekend()) {
                                                try {

                                                    html.append(this.holidayCheck(monthDataLeavePojo.getLeavePolicyId(), monthDataLeavePojo.getShortNameNp(), monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort()));
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                        } else {
                                            html.append("<td>")
                                                    .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort())
                                                    .append("</td>");
                                        }

                                    }

                                } else if (monthDailyLog != null) {
                                    html.append("<td>")
                                            .append("लग")
                                            .append("</td>");

                                } else if (monthDataPojo.getIsHoliday() && !monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave")) {
                                    List<String> parentOfficeCodeWithSelf = new ArrayList<>();
                                    parentOfficeCodeWithSelf.add("00");
                                    HolidayMapperPojo holidayMapperPojo = holidayMapper.holidayDetail(parentOfficeCodeWithSelf, newOne, fiscalYear.getId().toString());
                                    if (holidayMapperPojo != null) {
                                        html.append("<td>")
                                                .append(holidayMapperPojo.getShortNameNp() == null ? monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort() : holidayMapperPojo.getShortNameNp())
                                                .append("</td>");
                                    } else {
                                        html.append("<td>")
                                                .append("सा.बि.")
                                                .append("</td>");
                                    }

                                } else if (monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave") && monthDataPojo.getIsPresent()) {
                                    html.append("<td>")
                                            .append("उ")
                                            .append("</td>");

                                } else {
                                    html.append("<td>")
                                            .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort())
                                            .append("</td>");
                                }
                            }

                        }
                    } else {
                        html.append("<td>").append(" ")
                                .append("</td>");
                    }
                    newDate = newDate.plusDays(1);

                }
                html.append("</tr>");
                count.getAndIncrement();

            });
            html.append("</tbody>");
        }
        htmlString.add(html);
        htmlString.add(lateRemarks);

        return htmlString;

    }

    private String dateCheck(String date) throws ParseException {
        String datecheck = "";
        if ((date.split(":").length) > 2) {
            datecheck = datecheck.concat(date.split(":")[0])
                    .concat(":")
                    .concat(date.split(":")[1]);
        } else {
            datecheck = date;
        }
        System.out.println("checking" + datecheck);
        return datecheck;
    }

    private StringBuilder holidayCheck(Long leavePolicyId, String shotNameNp, String nepaliName) throws ParseException {
        StringBuilder html = new StringBuilder();
        if (leavePolicyRepo.findById(leavePolicyId).get().getCountPublicHoliday()) {
            if (shotNameNp != null) {
                html.append("<td>")
                        .append(shotNameNp)
                        .append("</td>");
            } else {
                html.append("<td>")
                        .append(nepaliName)
                        .append("</td>");
            }
        } else {
            html.append("<td>")
                    .append("सा.बि.")
                    .append("</td>");

        }
        return html;
    }

    private String holidayCheckNew(Long leavePolicyId, String shotNameNp, String nepaliName) throws ParseException {
        StringBuilder html = new StringBuilder();
        if (leavePolicyRepo.findById(leavePolicyId).get().getCountPublicHoliday()) {
            if (shotNameNp != null) {

                return shotNameNp;
            } else {

                return nepaliName;

            }
        } else {
            return "सा.बि.";

        }
    }

    private String holidayCheckStatus(Long leavePolicyId, String shotNameNp, String nepaliName) throws ParseException {
        if (leavePolicyRepo.findById(leavePolicyId).get().getCountPublicHoliday()) {
            if (shotNameNp != null) {
                return shotNameNp;
            } else {
                return nepaliName;
            }
        } else {
            return "सा.बि.";

        }
    }


    String getAttendanceData(List<EmployeeAttendanceMonthlyReportPojo> employeeAttendanceMonthlyReport, LocalDate fromDate, LocalDate toDate) {

        AtomicReference<String> attendanceStatus = new AtomicReference<>("");
        employeeAttendanceMonthlyReport.forEach(obj -> {
            List<MonthDataPojo> monthDataPojos = obj.getMonthlyAttendanceData();
            monthDataPojos.sort(Comparator.comparing(MonthDataPojo::getDateEn));

            LocalDate newDate = fromDate;
            Set<String> dateList = new HashSet<>();

            for (MonthDataPojo monthDays : monthDataPojos) {
                dateList.add(monthDays.getDateEn().toString());
            }
            while (newDate.compareTo(toDate) <= 0) {

                if (dateList.contains(newDate.toString())) {
                    LocalDate newOne = newDate;
                    IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();

                    MonthDataPojo monthDataPojo = monthDataPojos.stream().filter(x -> {
                        if (x.getDateEn().compareTo(newOne) == 0) {
                            return true;
                        } else
                            return false;
                    }).collect(Collectors.toList()).get(0);
//                        MonthDataPojo monthDataPojo= monthDataPojos.stream().filter(x->x.getDateEn().compareTo(newOne)==0).collect(Collectors.toList()).get(0);
                    MonthlyDailyLog monthDailyLog = (obj.getMonthlyDailyLogs().stream().filter(y -> y.getDateEn().compareTo(monthDataPojo.getDateEn()) == 0).collect(Collectors.toList()).isEmpty() ? null :
                            obj.getMonthlyDailyLogs().stream().filter(y -> y.getDateEn().compareTo(monthDataPojo.getDateEn()) == 0).collect(Collectors.toList()).get(0));

                    if (monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave")) {
                        MonthDataLeavePojo monthDataLeavePojo = obj.getMonthlyLeaveData().stream().filter(y -> y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0).collect(Collectors.toList()).isEmpty() ? null :
                                obj.getMonthlyLeaveData().stream().filter(y -> y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0).collect(Collectors.toList()).get(0);
//                            if(newOne.compareTo(dateConverter.convertToLocalDateViaInstant(new Date()))<0 && !monthDataPojo.getIsHoliday()){
//                                if (monthDataLeavePojo.getShortNameNp() != null) {
//                                    html.append("<td>")
//                                            .append(monthDataLeavePojo.getShortNameNp())
//                                            .append("</td>");
//                                }else {
//                                    html.append("<td>")
//                                            .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort())
//                                            .append("</td>");
//                                }
//                            }
                        if (monthDataPojo.getIsHoliday() && leavePolicyRepo.findById(monthDataLeavePojo.getLeavePolicyId()).get().getCountPublicHoliday()) {

                            if (monthDataLeavePojo.getShortNameNp() != null) {
                                attendanceStatus.set(monthDataLeavePojo.getShortNameNp());
                            } else {
                                attendanceStatus.set(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());
                            }
                        } else {

                            ShiftPojo shiftPojo = shiftService.getApplicableShiftByEmployeeCodeAndDate(obj.getPisCode(), tokenProcessorService.getOfficeCode(), newOne);
                            if (shiftPojo != null) {
                                ShiftDayPojo day = shiftPojo.getDays().get(0);
                                day.getShiftTimes().forEach(z -> {
                                    ShiftTimePojo time = z;
                                    //check for holiday
                                    List<String> parentOfficeCodeWithSelf = new ArrayList<>();
                                    parentOfficeCodeWithSelf.add("00");
                                    String gender = employeeAttendanceMapper.getEmployeeGender(obj.getPisCode(), tokenProcessorService.getOfficeCode());
                                    Long countHoliday = null;
                                    countHoliday = holidayMapper.checkHoliday(parentOfficeCodeWithSelf, newOne, leaveRequestMapper.getNepaliYear(Date.from(newOne.atStartOfDay(ZoneId.systemDefault()).toInstant())), gender);
                                    if (countHoliday != 0) {
                                        try {
                                            attendanceStatus.set(holidayCheckStatus(monthDataLeavePojo.getLeavePolicyId(), monthDataLeavePojo.getShortNameNp(), monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort()));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    } else if (monthDataLeavePojo.getShortNameNp() != null) {
                                        attendanceStatus.set(monthDataLeavePojo.getShortNameNp());
                                    } else {
                                        attendanceStatus.set(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());
                                    }
                                });
                                if (day.getIsWeekend()) {
                                    try {
                                        attendanceStatus.set(this.holidayCheckStatus(monthDataLeavePojo.getLeavePolicyId(), monthDataLeavePojo.getShortNameNp(), monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort()));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }

                            } else {
                                attendanceStatus.set(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());
                            }


                        }
                    } else if (monthDailyLog != null) {
                        attendanceStatus.set("लग");
                    } else if (monthDataPojo.getIsHoliday() && !monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave")) {
                        List<String> parentOfficeCodeWithSelf = new ArrayList<>();
                        parentOfficeCodeWithSelf.add("00");
                        HolidayMapperPojo holidayMapperPojo = holidayMapper.holidayDetail(parentOfficeCodeWithSelf, newOne, fiscalYear.getId().toString());
                        if (holidayMapperPojo != null) {
                            attendanceStatus.set(holidayMapperPojo.getShortNameNp() == null ? monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort() : holidayMapperPojo.getShortNameNp());
                        } else {
                            attendanceStatus.set("सा.बि.");
                        }

                    } else {
                        attendanceStatus.set(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());
                    }

                } else {
                    attendanceStatus.set(" ");
                }
                newDate = newDate.plusDays(1);

            }

        });
        return attendanceStatus.toString();
    }

}
