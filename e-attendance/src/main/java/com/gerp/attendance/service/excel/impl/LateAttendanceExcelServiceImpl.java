package com.gerp.attendance.service.excel.impl;

import com.gerp.attendance.Pojo.LateEmployeePojo;
import com.gerp.attendance.Pojo.report.ReportPojo;
import com.gerp.attendance.mapper.EmployeeAttendanceMapper;
import com.gerp.attendance.service.excel.LateAttendanceExcelService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LateAttendanceExcelServiceImpl implements LateAttendanceExcelService {
    @Autowired
    private EmployeeAttendanceMapper employeeAttendanceMapper;

    private Workbook workbook;
    private Sheet sheet;
    private int rowNumber;
    private List<String> headings = new ArrayList<>();
    private List<Integer> widths = new ArrayList<>();

    public LateAttendanceExcelServiceImpl() {
        this.rowNumber = 0;
        this.widths = Arrays.asList(
                10,
                40,
                40,
                20,
                20,
                20,
                20,
                20,
                40
        );
    }


    List<String>heading1 = Arrays.asList(
            "S.N",
            "Employee Name",
            "Designation",
            "Date",
            "Shift Checkin",
            "Shift Checkout",
            "Checkin",
            "Checkout",
            "Remark"
    );
    List<String>heading2 = Arrays.asList(
            "क्र.सं",
            "कर्मचारीको नाम",
            "पद",
            "मिति",
            "शिफ्ट चेकइन",
            "शिफ्ट चेकआउट",
            "चेक-इन समय",
            "चेक-आउट समय",
            "टिप्पणी"
    );

    @Override
    public Workbook loadDataToSheet(ReportPojo reportPojo,int reportType) {
        this.workbook = new XSSFWorkbook();
        if(reportType==0){
            this.headings=heading1;
        }else{
            this.headings=heading2;
        }
        this.rowNumber = 0;
        this.sheet = this.workbook.createSheet(LocalDate.now().toString());
        this.setHeadingAndWidth();
        CellStyle subCellStyleEnd = this.getCommonStyle();
        subCellStyleEnd.setAlignment(HorizontalAlignment.RIGHT);

        List<LateEmployeePojo> data = employeeAttendanceMapper.getAllExcelLateAttendance(
                reportPojo.getOfficeCode(),
                reportPojo.getSearchField()

        );

        int p = 1;
        for (LateEmployeePojo y : data) {
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
            this.createCell(row, q++, y.getDateEn().toString(), this.getCommonStyle());
            this.createCell(row, q++, y.getShiftCheckin()==null ?null :y.getShiftCheckin().toString(), this.getCommonStyle());
            this.createCell(row, q++, y.getShiftCheckout()==null?null :y.getShiftCheckout().toString(), this.getCommonStyle());
            this.createCell(row, q++, y.getCheckIn() == null ? null : y.getCheckIn().toString(), this.getCommonStyle());
            this.createCell(row, q++, y.getCheckOut() == null ? null : y.getCheckOut().toString(), this.getCommonStyle());
            this.createCell(row, q++, y.getLateRemarks() == null ? null : y.getLateRemarks(), this.getCommonStyle());
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
}
