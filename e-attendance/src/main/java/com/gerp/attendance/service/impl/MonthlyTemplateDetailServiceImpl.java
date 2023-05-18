package com.gerp.attendance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.FileConverterPojo;
import com.gerp.attendance.Pojo.HolidayMapperPojo;
import com.gerp.attendance.Pojo.MonthlyTemplatePojo;
import com.gerp.attendance.Pojo.OfficePojo;
import com.gerp.attendance.Pojo.report.EmployeeAttendanceMonthlyReportPojo;
import com.gerp.attendance.Pojo.report.MonthDataLeavePojo;
import com.gerp.attendance.Pojo.report.MonthDataPojo;
import com.gerp.attendance.Pojo.report.MonthlyDailyLog;
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
import com.gerp.attendance.service.MonthlyTemplateDetailService;
import com.gerp.attendance.service.ShiftService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.utils.WeekNepaliConstants;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.Collectors;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class MonthlyTemplateDetailServiceImpl implements MonthlyTemplateDetailService {
    private final TemplateEngine templateEngine;
    private final UserMgmtServiceData userMgmtServiceData;
    private final LeaveRequestMapper leaveRequestMapper;


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

    public MonthlyTemplateDetailServiceImpl(TemplateEngine templateEngine, UserMgmtServiceData userMgmtServiceData,
                                            LeaveRequestMapper leaveRequestMapper) {
        this.templateEngine = templateEngine;
        this.userMgmtServiceData = userMgmtServiceData;
        this.leaveRequestMapper=leaveRequestMapper;
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
    public byte[] generateMonthlyDetailPdf(GetRowsRequest paginatedRequest, String lang) {
        paginatedRequest.setLimit(1000);
        Page<EmployeeAttendanceMonthlyReportPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());
        if(tokenProcessorService.isGeneralUser()){
            paginatedRequest.getSearchField().put("pisCode",tokenProcessorService.getPisCode());

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
        List<String> headers = new ArrayList<>();
        List<String> headerEmployee = new ArrayList<>();
        int count = 0;
        Context context = new Context();
        StringBuilder heading = new StringBuilder();
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
            if (paginatedRequest.getSearchField().get("userType") != null) {
                String userType = userMgmtMapper.getUserType(1, paginatedRequest.getSearchField());
                headers.add(count, userMgmtMapper.getDateWiseFiscalYear(1,paginatedRequest.getFromDate(),paginatedRequest.getToDate()).concat("Monthly Detail Report of").concat(StringUtils.capitalize(userType).concat("Employee")));
            } else {
                headers.add(count, userMgmtMapper.getDateWiseFiscalYear(1,paginatedRequest.getFromDate(),paginatedRequest.getToDate()).concat("Monthly Detail Report of All Employee"));
            }
        } else {
            if (paginatedRequest.getSearchField().get("userType") != null) {
                String userType = userMgmtMapper.getUserType(0, paginatedRequest.getSearchField());
                headers.add(count, userMgmtMapper.getDateWiseFiscalYear(0,paginatedRequest.getFromDate(),paginatedRequest.getToDate()).concat("को (").concat(dateConverter.getMonth(paginatedRequest.getMonth())).concat(") को").concat(StringUtils.capitalize(userType).concat(" मासिक उपस्थिति जानकारी")));
            } else {
                headers.add(count, userMgmtMapper.getDateWiseFiscalYear(0,paginatedRequest.getFromDate(),paginatedRequest.getToDate()).concat("(").concat(dateConverter.getMonth(paginatedRequest.getMonth())).concat(") को मासिक उपस्थिति जानकारी"));
            }
        }
        monthlyTemplatePojo.setHeaders(headers);
        monthlyTemplatePojo.setHeaderEmployee(headerEmployee);
        this.getForMonthlyDetail(lang, page, paginatedRequest.getFromDate(), paginatedRequest.getToDate());

//        heading.append(topOfficeDetails.get(topOfficeDetails.size()-1));
        context.setVariable("header", monthlyTemplatePojo);

        String process = templateEngine.process("monthly_detail.html", context);
        Logger logger = LoggerFactory.getLogger(MonthlyTemplateDetailServiceImpl.class);
        logger.info(process);
        FileConverterPojo fileConverterPojo = new FileConverterPojo(process);
        return messagingServiceData.getFileConverter(fileConverterPojo);
    }

    private MonthlyTemplatePojo getForMonthlyDetail(String lang, Page<EmployeeAttendanceMonthlyReportPojo> employeeAttendanceMonthlyReportPojoPage, LocalDate fromDate, LocalDate toDate) {
        StringBuilder lateRemarks = new StringBuilder();

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
        } else if (lang.equalsIgnoreCase("1")) {
            LocalDate now = fromDate;
            int countHeader = 0;
            List<String> daysPojos = new ArrayList<>();
            while (now.compareTo(toDate) <= 0) {

                StringBuilder message = new StringBuilder();
                message.append(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(now.toString()).split("-")[2]));
                message.append("<br>");
                message.append(WeekNepaliConstants.getNepVal(now.getDayOfWeek().getValue()));
                daysPojos.add(countHeader, message.toString());
                now = now.plusDays(1);
                countHeader++;

            }
            monthlyTemplatePojo.setMonthDays(daysPojos);

        }

            List<EmployeeAttendanceMonthlyReportPojo> employeeAttendanceMonthlyReportPojoList = new ArrayList<>();
            employeeAttendanceMonthlyReportPojoPage.getRecords().forEach(obj -> {
                List<MonthDataPojo> monthDataPojos = obj.getMonthlyAttendanceData();
                monthDataPojos.sort(Comparator.comparing(MonthDataPojo::getDateEn));
                LocalDate newDate = fromDate;
                Set<String> dateList = new HashSet<>();
                List<String> attendanceDetail = new ArrayList<>();

                List<String> remarksList = new ArrayList<>();

                EmployeeAttendanceMonthlyReportPojo employeeAttendanceMonthlyReportPojo = new EmployeeAttendanceMonthlyReportPojo();

                for (MonthDataPojo monthDays : monthDataPojos) {
                    dateList.add(monthDays.getDateEn().toString());
                }
                int countRemarks=0;
                while (newDate.compareTo(toDate) <= 0) {
                    StringBuilder html = new StringBuilder();

                    if (dateList.contains(newDate.toString())) {
                        LocalDate newOne = newDate;
                        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();

                        MonthDataPojo monthDataPojo = monthDataPojos.stream().filter(x -> {
                            if (x.getDateEn().compareTo(newOne) == 0) {
                                return true;
                            } else
                                return false;
                        }).collect(Collectors.toList()).get(0);

                        if (monthDataPojo != null) {
                            if (monthDataPojo.getCheckin() != null && monthDataPojo.getShiftCheckin() != null) {
                                if (monthDataPojo.getCheckin().compareTo(monthDataPojo.getShiftCheckin()) > 0) {
                                    try {

                                        html.append(monthDataPojo.getCheckin() == null ? "" : (dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckin().toString()))))
                                                .append(monthDataPojo.getCheckout() == null ? "" : dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckout().toString())))
                                                .append("*");
                                        attendanceDetail.add(html.toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

//                                    lateRemarks.append("<span style=\"font-size:20px\">")
//                                            .append(StringUtils.capitalize(obj.getEmpNameNp()))
//                                            .append(": (" + dateConverter.convertBSToDevnagari(monthDataPojo.getDateNp()) + ")")
//                                            .append(":")
//                                            .append(monthDataPojo.getLateRemarks() == null ? "विवरण पेश गरिएको छैन ।" : monthDataPojo.getLateRemarks())
//                                            .append("</span>")
//                                            .append("<br>");
                                    remarksList.add(lateRemarks.toString());

                                    countRemarks++;
                                } else if (monthDataPojo.getCheckin() != null && monthDataPojo.getIsHoliday()) {

                                    try {
                                        html.append(monthDataPojo.getCheckin() == null ? "" : (dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckin().toString()))))
                                                .append(monthDataPojo.getCheckout() == null ? "" : dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckout().toString())))
                                                .append("*");
                                        attendanceDetail.add(html.toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    try {
                                        html.append(monthDataPojo.getCheckin() == null ? "" : (dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckin().toString()))))
                                                .append(monthDataPojo.getCheckout() == null ? "" : dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckout().toString())));
                                        attendanceDetail.add(html.toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                MonthlyDailyLog monthDailyLog = (obj.getMonthlyDailyLogs().stream().filter(y -> y.getDateEn().compareTo(monthDataPojo.getDateEn()) == 0).collect(Collectors.toList()).isEmpty() ? null :
                                        obj.getMonthlyDailyLogs().stream().filter(y -> y.getDateEn().compareTo(monthDataPojo.getDateEn()) == 0).collect(Collectors.toList()).get(0));

                                if (monthDataPojo.getAttendanceStatus().getEnum().getValueEnglish().equalsIgnoreCase("leave")) {
                                    MonthDataLeavePojo monthDataLeavePojo = obj.getMonthlyLeaveData().stream().filter(y -> y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0).collect(Collectors.toList()).isEmpty() ? null :
                                            obj.getMonthlyLeaveData().stream().filter(y -> y.getFromDateEn().compareTo(monthDataPojo.getDateEn()) * monthDataPojo.getDateEn().compareTo(y.getToDateEn()) >= 0).collect(Collectors.toList()).get(0);
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

                                                } else if (monthDataLeavePojo.getShortNameNp() != null) {
                                                    attendanceDetail.add(monthDataLeavePojo.getShortNameNp());

                                                } else {

                                                    attendanceDetail.add(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort());

                                                }
                                            });
                                            if (day.getIsWeekend()) {
                                                try {

                                                    attendanceDetail.add(this.holidayCheckNew(monthDataLeavePojo.getLeavePolicyId(), monthDataLeavePojo.getShortNameNp(), monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort()));
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
                            }

                        }

                    }
                    else {
                        attendanceDetail.add(" ");
                    }
                    newDate = newDate.plusDays(1);
                }
                    count.getAndIncrement();
                    employeeAttendanceMonthlyReportPojo.setShortNameNp(attendanceDetail);
                    if(lang.equalsIgnoreCase("0")){
                        employeeAttendanceMonthlyReportPojo.setSn(count.toString());
                    }else{
                        employeeAttendanceMonthlyReportPojo.setSn(dateConverter.convertToNepali(count.toString()));
                    }
                    employeeAttendanceMonthlyReportPojo.setEmpNameEn(obj.getEmpNameEn());
                    employeeAttendanceMonthlyReportPojo.setEmpNameNp(obj.getEmpNameNp());
                    employeeAttendanceMonthlyReportPojo.setFdNameEn(obj.getFdNameEn());
                    employeeAttendanceMonthlyReportPojo.setFdNameNp(obj.getFdNameNp());
                    employeeAttendanceMonthlyReportPojo.setPisCode(obj.getPisCode());
                    employeeAttendanceMonthlyReportPojo.setLanguage(lang);

                    employeeAttendanceMonthlyReportPojoList.add(employeeAttendanceMonthlyReportPojo);

                monthlyTemplatePojo.setEAttMonthReport(employeeAttendanceMonthlyReportPojoList);
            });


        return monthlyTemplatePojo;
    }

    private String dateCheck(String date) throws ParseException {
        String datecheck="";
        if((date.split(":").length)>2){
            datecheck=datecheck.concat(date.split(":")[0])
                    .concat(":")
                    .concat(date.split(":")[1]);
        }
        else {
            datecheck=date;
        }
        System.out.println("checking"+datecheck);
        return datecheck;
    }

    private StringBuilder holidayCheck (Long leavePolicyId,String shotNameNp,String nepaliName) throws ParseException {
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
    private String holidayCheckNew (Long leavePolicyId,String shotNameNp,String nepaliName) throws ParseException {
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
}
