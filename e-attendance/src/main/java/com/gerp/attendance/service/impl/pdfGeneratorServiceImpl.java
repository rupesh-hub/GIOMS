package com.gerp.attendance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.FileConverterPojo;
import com.gerp.attendance.Pojo.HolidayMapperPojo;
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
import com.gerp.attendance.service.PdfGeneratorService;
import com.gerp.attendance.service.ShiftService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.utils.WeekNepaliConstants;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class pdfGeneratorServiceImpl implements PdfGeneratorService {

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

    public pdfGeneratorServiceImpl(TemplateEngine templateEngine, UserMgmtServiceData userMgmtServiceData,
                                   LeaveRequestMapper leaveRequestMapper) {
        this.templateEngine = templateEngine;
        this.userMgmtServiceData = userMgmtServiceData;
        this.leaveRequestMapper = leaveRequestMapper;
    }

//    @Override
//    public byte[] generatePdfForInbox(GetRowsRequest paginatedRequest, String lang) {
//        paginatedRequest.setLimit(1000);
//        Page<ReceivedLetterResponsePojo> receivedLetterResponsePojoPage = receivedLetterService.filterData(paginatedRequest);
//        StringBuilder html;
//        try {
//            html = getBodyContain(lang, receivedLetterResponsePojoPage);
//        }catch (Exception e){
//            throw new CustomException(e.getMessage());
//        }
//
//        Context context = new Context();
//        StringBuilder heading = new StringBuilder();
//        if (lang.equalsIgnoreCase("EN")){
//            heading.append("NEPAL GOVERNMENT <br>");
//        }else {
//            heading.append("नेपाल सरकार <br>");
//        }
//        List<String> topOfficeDetails = topOfficeDetails(tokenProcessorService.getOfficeCode(), new ArrayList<>(), lang,1);
//        for (int i = topOfficeDetails.size()-1 ; i >= 0 ; i--){
//            if (i == topOfficeDetails.size()-2){
//                heading.append("<span style=\"font-size:20px\">").append(topOfficeDetails.get(i)).append("</span>");
//            }else {
//                heading.append(topOfficeDetails.get(i));
//            }
//            heading.append("<br>");
//        }
////        heading.append(topOfficeDetails.get(topOfficeDetails.size()-1));
//        context.setVariable("header", heading.toString());
//        context.setVariable("body",html);
//        String process = templateEngine.process("header.html", context);
//        FileConverterPojo fileConverterPojo = new FileConverterPojo(process);
//        return convertHtlToFileProxy.getFileConverter(fileConverterPojo);
//    }

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
//        paginatedRequest.setLimit(1000);
//        Page<EmployeeAttendanceMonthlyReportPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
//        paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());
//        page = employeeAttendanceMapper.filterDataPaginatedMonthly(
//                page,
//                paginatedRequest.getOfficeCode(),
//                paginatedRequest.getUserStatus(),
//                paginatedRequest.getSearchField()
//        );
//        page.getRecords().forEach(x -> {
////            paginatedRequest.setSearchField(new HashMap<>());
//
//            if (paginatedRequest.getForLeave()) {
////                paginatedRequest.getSearchField().put("attendanceType",AttendanceStatus.LEAVE);
//                x.setMonthlyLeaveData(
//                        employeeAttendanceMapper.getMonthLeaveData(
//                                x.getPisCode(),
//                                paginatedRequest.getFromDate(),
//                                paginatedRequest.getToDate()
//                        )
//                );
//            }
//            if (paginatedRequest.getForKaaj()) {
////                paginatedRequest.getSearchField().put("attendanceType",AttendanceStatus.KAAJ);
//                x.setMonthlyKaajData(
//                        employeeAttendanceMapper.getMonthKaajData(
//                                x.getPisCode(),
//                                paginatedRequest.getFromDate(),
//                                paginatedRequest.getToDate()
//                        )
//                );
//            }
//            x.setMonthlyAttendanceData(
//                    employeeAttendanceMapper.getMonthAttendanceData(
//                            x.getPisCode(),
//                            paginatedRequest.getFromDate(),
//                            paginatedRequest.getToDate()
//                    )
//            );
//        });

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
//                paginatedRequest.getSearchField().put("attendanceType",AttendanceStatus.LEAVE);
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
        try {
            html = getBodyContain(lang, page, paginatedRequest.getFromDate(), paginatedRequest.getToDate());
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
                heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getActiveFiscalYear(1)).append("Monthly Report of").append(StringUtils.capitalize(userType)).append("Employee").append("</span>");
            } else {

                heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getActiveFiscalYear(1)).append("Monthly Report of All Employee").append("</span>");

            }
        } else {
            if (paginatedRequest.getSearchField().get("userType") != null) {
                String userType = userMgmtMapper.getUserType(0, paginatedRequest.getSearchField());
                heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getActiveFiscalYear(0)).append("को (").append(dateConverter.getMonth(paginatedRequest.getMonth())).append(") को").append(StringUtils.capitalize(userType)).append(" मासिक उपस्थिति").append("</span>");
            } else {

                heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getActiveFiscalYear(0)).append("(").append(dateConverter.getMonth(paginatedRequest.getMonth())).append(") को मासिक उपस्थिति").append("</span>");

            }
        }
//        heading.append(topOfficeDetails.get(topOfficeDetails.size()-1));
        context.setVariable("header", heading.toString());
        context.setVariable("body", html);
        String process = templateEngine.process("header.html", context);
        Logger logger = LoggerFactory.getLogger(pdfGeneratorServiceImpl.class);
        logger.info(process);
        FileConverterPojo fileConverterPojo = new FileConverterPojo(process);
        return messagingServiceData.getFileConverter(fileConverterPojo);
    }


    private StringBuilder getBodyContain(String lang, Page<EmployeeAttendanceMonthlyReportPojo> employeeAttendanceMonthlyReportPojoPage, LocalDate fromDate, LocalDate toDate) {
        StringBuilder html = new StringBuilder();
        AtomicInteger count = new AtomicInteger(1);
        if (lang.equalsIgnoreCase("0")) {
            html.append("<thead><tr>" +
                    "<th>S.N</th>" +
                    "<th>Pis Code</th>" +
                    "<th>Employee Name</th>");
            LocalDate now = fromDate;
            int countHeader = 0;
            while (now.compareTo(toDate) <= 0) {
                html.append("<th>").append(dateConverter.convertAdToBs(now.toString()).split("-")[2])
                        .append("<br>").append(WeekNepaliConstants.getEngVal(now.getDayOfWeek().getValue()))
                        .append("</th>");
                now = now.plusDays(1);
                countHeader++;

            }
            html.append("</tr></thead>");

            html.append("<tbody>");
            employeeAttendanceMonthlyReportPojoPage.getRecords().forEach(obj -> {
                List<MonthDataPojo> monthDataPojos = obj.getMonthlyAttendanceData();
                monthDataPojos.sort(Comparator.comparing(MonthDataPojo::getDateEn));
                html.append("<tr> <td>").append(count.get()).append("</td>")
//                html.append("<tr>")
                        .append("<td>").append(obj.getPisCode()).append("</td>")
                        .append("<td>").append(obj.getEmpNameEn())
                        .append("(" + StringUtils.capitalize(obj.getFdNameEn() == null ? "-" : obj.getFdNameEn()) + ")").append("</td>");
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

                        } else {
                            html.append("<td>")
                                    .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort())
                                    .append("</td>");
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

            html.append("<thead><tr>" +
                    "<th>क्र.सं.</th>" +
                    "<th>क.सं.नं.</th>" +
                    "<th>कर्मचारी विवरण</th>");
            LocalDate now = fromDate;
            int countHeader = 0;
            while (now.compareTo(toDate) <= 0) {
                html.append("<th>").append(dateConverter.convertToNepali(dateConverter.convertAdToBs(now.toString()).split("-")[2]))
                        .append("<br>").append(WeekNepaliConstants.getNepVal(now.getDayOfWeek().getValue())).
                        append("</th>");
                now = now.plusDays(1);
                countHeader++;

            }
            html.append("</tr></thead>");

            html.append("<tbody>");
            employeeAttendanceMonthlyReportPojoPage.getRecords().forEach(obj -> {
                List<MonthDataPojo> monthDataPojos = obj.getMonthlyAttendanceData();
                monthDataPojos.sort(Comparator.comparing(MonthDataPojo::getDateEn));
                html.append("<tr> <td>").append(dateConverter.convertToNepali(String.valueOf(count.get()))).append("</td>")
                        .append("<td>").append(dateConverter.convertToNepali(obj.getPisCode())).append("</td>")
                        .append("<td>").append(obj.getEmpNameNp())
                        .append("(" + StringUtils.capitalize(obj.getFdNameNp() == null ? "-" : obj.getFdNameNp()) + ")").append("</td>");
                LocalDate newDate = fromDate;
                Set<String> dateList = new HashSet<>();
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

                        } else {
                            html.append("<td>")
                                    .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort())
                                    .append("</td>");
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
//        else{
//            html.append("<tr>" +
//                    "<th>क्र.सं.</th>" +
//                    "<th>पीआईएस कोड</th>" +
//                    "<th>कर्मचारी विवरण</th>");
//            LocalDate now = fromDate;
//            int countHeader=0;
//            while (now.compareTo(toDate) < 0) {
//                html.append("<th>").append(dateConverter.convertBSToDevnagari(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(now.toString())))).append("</th>");
//                now = now.plusDays(1);
//                countHeader++;
//
//            }
//            html.append("</tr>");
//            int finalCountHeader = countHeader;
//            employeeAttendanceMonthlyReportPojoPage.getRecords().forEach(obj -> {
//
//                html.append("<tr> <td>").append(count.get()).append("</td>")
//                        .append("<td>").append(dateConverter.convertToNepali(obj.getPisCode())).append("</td>")
//                        .append("<td>").append(obj.getEmpNameNp()).append("</td>");
//                AtomicInteger countmonth= new AtomicInteger();
//                obj.getMonthlyAttendanceData().forEach(monthDays -> {
//                    html.append("<td>").append(monthDays.getAttendanceStatus().getEnum().getValueNepaliShort()).append("</td>");
//                    countmonth.getAndIncrement();
//                });
//                while(finalCountHeader > countmonth.get()){
//                    html.append("<td>").append("-").append("</td>");
//                    countmonth.getAndIncrement();
//
//                }
//
//                count.getAndIncrement();
//            });
//            html.append("</tbody>");
//        }
//        }else {
//            html.append("<thead><tr>" +
//                    "<th>क्र.सं.</th>" +
//                    "<th>दर्ता नम्बर </th>" +
//                    "<th>दर्ता मिति (वि.सं.)</th>" +
//                    "<th>चलानी नम्बर</th>" +
//                    "<th>पत्र संख्या</th>" +
//                    "<th>पत्रको मिति (वि.सं.)</th>" +
////                    "<th>गोपनियता</th>" +
////                    "<th>प्राथमिकता</th>" +
//                    "<th>पठाउने व्यक्ति/कार्यालय</th>" +
//                    "<th>विषय</th></tr></thead><tbody style=\"font-size: 12px;\">");
//            receivedLetterResponsePojoPage.getRecords().forEach(obj->{
//                String senderName = "";
//                if (obj.getEntryType() != null && obj.getEntryType()){
//                    senderName = obj.getDetails().getManualSenderName();
//                }else {
//                    senderName = obj.getSenderOfficeNameNp();
//                }
//                html.append("<tr> <td>").append(numericConverter.convertEnglishNumbersToNepaliNumbers(count.get()+"")).append("</td>")
//                        .append("<td>").append(obj.getRegistrationNo()).append("</td>")
//                        .append("<td>").append(dateConverter.convertBSToDevnagari(obj.getCreatedDateNp())).append("</td>")
//                        .append("<td>").append(obj.getDispatchNo() == null?"-":numericConverter.convertEnglishNumbersToNepaliNumbers(obj.getDispatchNo())).append("</td>")
//                        .append("<td>").append(obj.getReferenceNo() == null?"-":numericConverter.convertEnglishNumbersToNepaliNumbers(obj.getReferenceNo())).append("</td>")
//                        .append("<td>").append(dateConverter.convertBSToDevnagari(obj.getDispatchDateNp())).append("</td>")
////                        .append("<td>").append(obj.getLetterPrivacy().getEnum().getValueNepali()).append("</td>")
////                        .append("<td>").append(obj.getLetterPriority().getEnum().getValueNepali()).append("</td>")
//                        .append("<td>").append(senderName).append("</td>")
//                        .append("<td>").append(obj.getSubject()).append("</td>");
//                count.getAndIncrement();
//            });
//            html.append("</tbody>");
//        }

        html.append("</tr>");

        return html;
    }

    @Override
    public byte[] generateMonthlyDetailPdf(GetRowsRequest paginatedRequest, String lang) {
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
//        try {
            html = getForMonthlyDetail(lang, page, paginatedRequest.getFromDate(), paginatedRequest.getToDate()).get(0);
            lateRemarks = getForMonthlyDetail(lang, page, paginatedRequest.getFromDate(), paginatedRequest.getToDate()).get(1);
//        } catch (Exception e) {
//            throw new CustomException(e.getMessage());
//        }

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
            if (paginatedRequest.getSearchField() != null) {
                if (paginatedRequest.getSearchField().get("userType") != null) {
                    String userType = userMgmtMapper.getUserType(1, paginatedRequest.getSearchField());
                    heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getDateWiseFiscalYear(1, paginatedRequest.getFromDate(), paginatedRequest.getToDate())).append("Monthly Detail Report of").append(StringUtils.capitalize(userType)).append("Employee").append("</span>");
                } else {

                    heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getDateWiseFiscalYear(1, paginatedRequest.getFromDate(), paginatedRequest.getToDate())).append("Monthly Detail Report of All Employee").append("</span>");

                }
            } else {

                heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getDateWiseFiscalYear(1, paginatedRequest.getFromDate(), paginatedRequest.getToDate())).append("Monthly Detail Report of All Employee").append("</span>");

            }
        } else {
            if (paginatedRequest.getSearchField() != null) {
                if (paginatedRequest.getSearchField().get("userType") != null) {
                    String userType = userMgmtMapper.getUserType(0, paginatedRequest.getSearchField());
                    heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getDateWiseFiscalYear(0, paginatedRequest.getFromDate(), paginatedRequest.getToDate())).append("(").append(dateConverter.getMonth(paginatedRequest.getMonth())).append(")").append(StringUtils.capitalize(userType)).append("को मासिक उपस्थिति जानकारी").append("</span>");
                } else {

                    heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getDateWiseFiscalYear(0, paginatedRequest.getFromDate(), paginatedRequest.getToDate())).append("(").append(dateConverter.getMonth(paginatedRequest.getMonth())).append(") को मासिक उपस्थिति जानकारी").append("</span>");

                }
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
                        .append("<td>").append(obj.getEmployeeCode()).append("</td>")
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
                                } else if (monthDataPojo.getCheckin() != null && monthDataPojo.getIsHoliday()) {

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
                                    System.out.println("eror"+obj.getEmployeeCode());

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
                                        if (monthDataPojo.getAttendanceStatus().name().equalsIgnoreCase(String.valueOf(AttendanceStatus.ATTENDANCE_ON_HOLIDAY))) {
                                            try {
                                                html.append("<td>")
                                                        .append("(")
                                                        .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort() + " ")
                                                        .append(")")
                                                        .append("<div>")
                                                        .append(monthDataPojo.getCheckin() == null ? "" : (dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckin().toString()))))
                                                        .append("</div>")
                                                        .append("<div>")
                                                        .append(monthDataPojo.getCheckout() == null ? "" : dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckout().toString())))
                                                        .append("</div>")
                                                        .append("</td>");
//                                                 .append("arun")
                                            } catch (ParseException e) {
                                                throw new RuntimeException(e);
                                            }
                                        } else {
                                            html.append("<td>")
                                                    .append("सा.बि.")
//                                                    .append("arun.")
                                                    .append("</td>");
                                        }

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
                    System.out.println("days"+newDate);

                }
                html.append("</tr>");
                count.getAndIncrement();
                System.out.println("dasdf");

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
                        .append("<td>").append(dateConverter.convertToNepali(obj.getEmployeeCode())).append("</td>")
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
                                } else if (monthDataPojo.getCheckin() != null && monthDataPojo.getIsHoliday()) {

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
                                        if (monthDataPojo.getAttendanceStatus().name().equalsIgnoreCase(String.valueOf(AttendanceStatus.ATTENDANCE_ON_HOLIDAY))) {
                                            try {
                                                html.append("<td>")
                                                        .append("(")
                                                        .append(monthDataPojo.getAttendanceStatus().getEnum().getValueNepaliShort() + " ")
                                                        .append(")")
                                                        .append("<div>")
                                                        .append(monthDataPojo.getCheckin() == null ? "" : (dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckin().toString()))))
                                                        .append("</div>")
                                                        .append("<div>")
                                                        .append(monthDataPojo.getCheckout() == null ? "" : dateConverter.convertToNepali(this.dateCheck(monthDataPojo.getCheckout().toString())))
                                                        .append("</div>")
                                                        .append("</td>");
//                                                        .append("arun");
                                            } catch (ParseException e) {
                                                throw new RuntimeException(e);
                                            }
                                        } else {
                                            html.append("<td>")
                                                    .append("सा.बि.")
                                                    .append("</td>");
                                        }

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
}
