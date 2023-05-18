package com.gerp.attendance.service.excel.impl;

import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.report.*;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.EmployeeAttendanceMapper;
import com.gerp.attendance.service.excel.SummaryExcelService;
import com.gerp.attendance.service.excel.YearlyReportExcelService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.utils.DateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Date;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
public class YearlyReportExcelServiceImpl implements YearlyReportExcelService {

    private Workbook workbook;
    private Sheet sheet;
    private int rowNumber;
    private List<String> headings = new ArrayList<>();
    private List<Integer> widths = new ArrayList<>();

    @Autowired
    private DateUtil dateUtils;

    @Autowired
    private EmployeeAttendanceMapper employeeAttendanceMapper;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;


    public YearlyReportExcelServiceImpl() {
        this.rowNumber = 0;
        this.widths = Arrays.asList(
                10,
                30,
                30,
                10,
                20,
                20

        );
    }

    List<String>heading1 = Arrays.asList(
            "S.N",
            "Employee",
            "Designation",
            "Month",
            "Date",
            "Attendance Status"

    );
    List<String> heading2 =Arrays.asList(
            "क्र.सं",
            "कर्मचारीको नाम",
            "पद",
            "महिना",
            "उपस्थिति स्थिति"
    );
    @Override
    public Workbook loadDataToSheet(Integer year, String pisCode, String officeCode, String fiscalYear, int reportType) {
        this.workbook = new XSSFWorkbook();
        if(reportType==0){
            this.headings=heading1;
        }else{
            this.headings=heading2;
        }
        this.rowNumber = 0;
        this.sheet = this.workbook.createSheet(year.toString());
        this.setHeadingAndWidth();
        CellStyle subCellStyleEnd = this.getCommonStyle();
        subCellStyleEnd.setAlignment(HorizontalAlignment.RIGHT);




        List<EmployeeAttendanceMonthlyReportPojo> employeeList= employeeAttendanceMapper.monthlyAttendanceData(tokenProcessorService.getOfficeCode(),pisCode);
       employeeList.stream().forEach(x->{
            List<EmployeeMonthlyAttendancePojo> employeeAttendanceList=new ArrayList<>();
            for(Integer i=1;i<=12;i++){

                DatesPojo monthStartAndEnd=employeeAttendanceMapper.getStartAndEndDate(i,year);
                List<EmployeeAttendanceNewMonthlyPojo> employeeMonthly= employeeAttendanceMapper.getMonthlyAttendance(x.getPisCode(),tokenProcessorService.getOfficeCode(),userMgmtServiceData.findActiveFiscalYear().getId().toString(),monthStartAndEnd.getFromDate(),monthStartAndEnd.getToDate());
                EmployeeMonthlyAttendancePojo employeeMonthlyAttendancePojo=new EmployeeMonthlyAttendancePojo().builder()
                        .month(i.toString())
                        .monthlyData(employeeMonthly==null?null:employeeMonthly)
                        .build();
                employeeAttendanceList.add(employeeMonthlyAttendancePojo);


            }
            x.setMonthlyAttendance(employeeAttendanceList);

        });



//            sectionNamePojos.stream().forEach(x-> {
//                        SectionMinimalPojo sectionMinimalPojo = userMgmtServiceData.getSectionEmployee(x.getId());
//                if(sectionMinimalPojo !=null) {
//                    if (!sectionMinimalPojo.getEmployeeList().isEmpty()) {
//                        sectionDetails.addAll(sectionMinimalPojo.getEmployeeList());
//                    }
//                }
//                    });
//        SectionMinimalPojo sectionMinimalPojo = userMgmtServiceData.getSectionEmployee(Long.parseLong(reportPojo.getSectionCode()));
//        sectionDetails.stream().forEach(x->{
//            List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(reportPojo.getOfficeCode());
//            Set<Long> shifts=shiftService.getEmployeeShifts(x.getPisCode(),reportPojo.getOfficeCode(),reportPojo.getFromDate(),reportPojo.getToDate());
//
//            EmployeeAttendanceSummaryDataPojo employeeAttendanceSummaryDataPojo=employeeAttendanceMapper.getSummaryData(
//                    reportPojo.getFiscalYear().toString(),
//                    x.getPisCode(),
//                    shifts,
//                    parentOfficeCodeWithSelf,
//                    reportPojo.getFromDate(),
//                    reportPojo.getToDate(),
//                    reportPojo.getSearchField()
//            );
//            if(employeeAttendanceSummaryDataPojo!=null) {
//                EmployeeAttendanceTotalSum sum = employeeAttendanceMapper.getSumForFilter(
//                        x.getPisCode(),
//                        AttendanceStatus.DEVICE.toString(),
//                        AttendanceStatus.MA.toString(),
//                        reportPojo.getFromDate(),
//                        reportPojo.getToDate(),
//                        reportPojo.getSearchField()
//                );
//
//                if (sum != null) {
//                    employeeAttendanceSummaryDataPojo.setTotalExtraTime(sum.getTotalExtraTime());
//                    employeeAttendanceSummaryDataPojo.setTotalLateCheckin(sum.getTotalLateCheckin());
//                    employeeAttendanceSummaryDataPojo.setTotalEarlyCheckout(sum.getTotalEarlyCheckout());
//                    employeeAttendanceSummaryDataPojo.setTotalEarlyCheckin(sum.getTotalEarlyCheckin());
//                    employeeAttendanceSummaryDataPojo.setPresentInHoliday(sum.getPresentInHoliday());
//                }
//                employeeAttendanceSummaryDataPojo.setEmployeeNameEn(x.getEmployeeNameEn());
//                employeeAttendanceSummaryDataPojo.setEmployeeNameNp(x.getEmployeeNameNp());
//                employeeAttendanceSummaryDataPojo.setEmployeeDesignationEn(x.getFunctionalDesignation().getName() == null ? null : x.getFunctionalDesignation().getName());
//                employeeAttendanceSummaryDataPojo.setEmployeeDesignationNp(x.getFunctionalDesignation().getNameN() == null ? null : x.getFunctionalDesignation().getNameN());
//                employeeAttendanceSummaryDataPojos.add(employeeAttendanceSummaryDataPojo);
//            }
//        });

        int p = 1;
        for ( EmployeeAttendanceMonthlyReportPojo y : employeeList) {
            Row row = sheet.createRow(rowNumber++);

            int q = 0;

            this.createCell(row, q++, p, this.getCommonStyle());
            if(reportType==0) {
                this.createCell(row, q++, y.getEmpNameEn(), this.getCommonStyle());
                this.createCell(row, q++, y.getFdNameEn(), this.getCommonStyle());
            }else{
                this.createCell(row, q++, y.getEmpNameNp(), this.getCommonStyle());
                this.createCell(row, q++, y.getFdNameNp(), this.getCommonStyle());
            }

//            for (EmployeeMonthlyAttendancePojo z : y.getMonthlyAttendance()) {
//                int count=q++;
//                this.createCell(sheet.getRow(0),count,z.getMonthlyData(),this.getHeaderStyle());
////                this.createCell(row, count, z.getIsPresent().toString(), this.getCommonStyle());
//
//            }
//            this.createCell(row, q++, y.getm().toString(), this.getCommonStyle());
//            this.createCell(row, q++, y.getWorkingDays().toString(), this.getCommonStyle());
//            this.createCell(row, q++, y.getTotalLateCheckin()==null?null:y.getTotalLateCheckin().toString(), this.getCommonStyle());
//            this.createCell(row, q++, y.getTotalEarlyCheckin()==null?null:y.getTotalEarlyCheckin().toString(), this.getCommonStyle());
//            this.createCell(row, q++, y.getTotalEarlyCheckout()==null?null:y.getTotalEarlyCheckout().toString(), this.getCommonStyle());
//            this.createCell(row, q++, y.getWorkedHours()==null?null:y.getWorkedHours().toString(), this.getCommonStyle());
//            this.createCell(row, q++, y.getTotalWorkingHour().toString(), this.getCommonStyle());
//            this.createCell(row, q++, y.getPresentInHoliday()==null?null:y.getPresentInHoliday().toString(), this.getCommonStyle());
//            this.createCell(row, q++, y.getWeekendDays()==null?null:y.getWeekendDays().toString(), this.getCommonStyle());
//            this.createCell(row, q++, y.getHolidayCount()==null?null:y.getHolidayCount().toString(), this.getCommonStyle());
//            this.createCell(row, q++, y.getKaaj()==null?null:y.getKaaj().toString(), this.getCommonStyle());
//            this.createCell(row, q++, y.getLeaveTaken()==null?null:y.getLeaveTaken().toString(), this.getCommonStyle());

            p++;
        }
        return this.workbook;
    }


    private String checkNull(Long value) {
        if (value == null)
            return "0";
        return value.toString();
    }

    private String checkNull(BigInteger value) {
        if (value == null)
            return "0";
        return value.toString();
    }

    private void setHeadingAndWidth() {
        Row headerRow = sheet.createRow(rowNumber++);
        for (int i = 0; i < headings.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headings.get(i));
            cell.setCellStyle(this.getHeaderStyle());
            this.sheet.setDefaultColumnWidth(widths.get(i));
        }
    }

    private CellStyle getHeaderStyle() {
        CellStyle style = this.workbook.createCellStyle();
        style.setFont(this.getFont());
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle getCommonStyle() {
        CellStyle style = this.workbook.createCellStyle();
        style.setFont(this.getCommonFont());
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private Font getFont() {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        return font;
    }

    private Font getCommonFont() {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        return font;
    }

    private void createCell(Row row, int i, String value, CellStyle style) {
        Cell cell = row.createCell(i);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }


    private void createCell(Row row, int i, int value, CellStyle style) {
        Cell cell = row.createCell(i);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int i, Long value, CellStyle style) {
        Cell cell = row.createCell(i);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
