package com.gerp.attendance.service.excel.impl;

import com.gerp.attendance.Pojo.report.*;
import com.gerp.attendance.mapper.EmployeeAttendanceMapper;
import com.gerp.attendance.service.excel.MonthlyAttendanceExcelService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.utils.DateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Date;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
public class MonthlyAttendanceExcelServiceImpl implements MonthlyAttendanceExcelService {

    @Autowired
    private EmployeeAttendanceMapper employeeAttendanceMapper;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private DateUtil dateUtils;

    private Workbook workbook;
    private Sheet sheet;
    private int rowNumber;
    private List<String> headings = new ArrayList<>();
    private List<Integer> widths = new ArrayList<>();

    public MonthlyAttendanceExcelServiceImpl(){
        this.rowNumber = 0;
        this.headings = Arrays.asList(
                "S.N",
                "Pis Code",
                "Employee Name",
                "Designation"
        );
        this.widths = Arrays.asList(
                10,
                20,
                60,
                50
        );
    }

    @Override
    public Workbook loadDataToSheet(ReportPojo reportPojo) {
        this.workbook = new XSSFWorkbook();
        this.rowNumber = 0;
        this.sheet = this.workbook.createSheet(reportPojo.getFromDate()+" - "+reportPojo.getToDate());
        this.setHeadingAndWidth();
        CellStyle subCellStyleEnd = this.getCommonStyle();
        subCellStyleEnd.setAlignment(HorizontalAlignment.RIGHT);

        List<EmployeeAttendanceMonthlyReportPojo> employeeAttendanceMonthlyReportPojos = employeeAttendanceMapper.filterExcelDataPaginatedMonthly(
                reportPojo.getOfficeCode(),
                reportPojo.getSearchField()
        );
        employeeAttendanceMonthlyReportPojos.forEach(x->{
            x.setMonthlyAttendanceData(
                    employeeAttendanceMapper.getMonthAttendanceData(
                            x.getPisCode(),
                            tokenProcessorService.getOfficeCode(),
                            x.getIsJoin(),
                            x.getIsLeft(),
                            reportPojo.getFromDate(),
                            reportPojo.getToDate()
                    )
            );

            if(reportPojo.getForLeave()){
                x.setMonthlyLeaveData(
                        employeeAttendanceMapper.getMonthLeaveData(
                                x.getPisCode(),
                                reportPojo.getFromDate(),
                                reportPojo.getToDate()
                        )
                );
            }

            if(reportPojo.getForKaaj()){
                x.setMonthlyKaajData(
                        employeeAttendanceMapper.getMonthKaajData(
                                x.getPisCode(),
                                reportPojo.getFromDate(),
                                reportPojo.getToDate()
                        )
                );
            }
        });


            int p=1;
            for (EmployeeAttendanceMonthlyReportPojo y : employeeAttendanceMonthlyReportPojos) {
                Row row = sheet.createRow(rowNumber++);
                int q=0;
//                int  count=0;
                this.createCell(row ,q++, p, this.getCommonStyle());
                this.createCell(row , q++, y.getPisCode(),this.getCommonStyle());
                this.createCell(row , q++, y.getEmpNameEn(),this.getCommonStyle());
                this.createCell(row , q++, y.getFdNameEn(), this.getCommonStyle());

                for (MonthDataPojo z : y.getMonthlyAttendanceData()) {
                    int count=q++;
                    this.createCell(sheet.getRow(0),count,dateUtils.days(Date.from((z.getDateEn()).atStartOfDay(ZoneId.systemDefault()).toInstant())).concat("(").concat(z.getDateEn().toString()).concat(")"),this.getHeaderStyle());
                    this.createCell(row, count, z.getIsPresent().toString(), this.getCommonStyle());

                }

                if(reportPojo.getForLeave()){
                    for (MonthDataLeavePojo w : y.getMonthlyLeaveData()) {
                    int counts=q++;
                        this.createCell(sheet.getRow(0),counts,"Leave Taken",this.getHeaderStyle());
                        this.createCell(row, counts,w.getFromDateEn().toString().concat(" - ").concat(w.getToDateEn().toString())==null?null :w.getFromDateEn().toString().concat(" - ").concat(w.getToDateEn().toString()), this.getCommonStyle());

//                        if(w.getIsHoliday()){
//                            this.createCell(sheet.getRow(0),counts++,"Holiday Name",this.getHeaderStyle());
//                            this.createCell(row, counts++,w.getHolidayNameEn()==null?null :w.getHolidayNameEn(), this.getCommonStyle());
//                        }else{
//                            this.createCell(sheet.getRow(0),counts++,"Leave Name",this.getHeaderStyle());
//                            this.createCell(row, counts++,w.getLeaveNameEn() ==null? null :w.getLeaveNameEn(), this.getCommonStyle());
//                        }
//                        q++;
                    }
                }

                if(reportPojo.getForKaaj()){
                    for (MonthDataKaajPojo x : y.getMonthlyKaajData()) {
                        int  counts=q++;
                        this.createCell(sheet.getRow(0),counts,"Kaaj Date",this.getHeaderStyle());
                        this.createCell(row, counts,x.getFromDateEn().toString().concat(" - ").concat(x.getToDateEn().toString())==null? null :x.getFromDateEn().toString().concat(" - ").concat(x.getToDateEn().toString()), this.getCommonStyle());

//                        int count2=count++;
//                        this.createCell(sheet.getRow(0),count2,"Kaaj Name",this.getHeaderStyle());
//                        this.createCell(row, count2,x.getKaajTypeNameEn(), this.getCommonStyle());

                    }
                }


                p++;
            }
//            if(reportPojo.getForLeave()){
//                x.setMonthlyLeaveData(
//                        employeeAttendanceMapper.getMonthLeaveData(
//                                x.getPisCode(),
//                                reportPojo.getFromDate(),
//                                reportPojo.getToDate()
//                        )
//                );
//            }
//            if(reportPojo.getForKaaj()){
//                x.setMonthlyKaajData(
//                        employeeAttendanceMapper.getMonthKaajData(
//                                x.getPisCode(),
//                                reportPojo.getFromDate(),
//                                reportPojo.getToDate()
//                        )
//                );
//            }

        return workbook;

    }

    private String checkNull(Long value) {
        if(value == null)
            return "0";
        return value.toString();
    }

    private String checkNull(BigInteger value) {
        if(value == null)
            return "0";
        return value.toString();
    }

    private void setHeadingAndWidth(){
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

    private void createCellForHeader(Row row, int i, String value, CellStyle style) {
        Cell cell = row.createCell(i);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
