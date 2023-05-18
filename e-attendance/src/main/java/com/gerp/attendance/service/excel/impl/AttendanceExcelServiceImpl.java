package com.gerp.attendance.service.excel.impl;

import com.gerp.attendance.Pojo.LeaveReportDataPojo;
import com.gerp.attendance.Pojo.report.EmployeeAttendanceReportDataPojo;
import com.gerp.attendance.Pojo.report.ReportPojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.EmployeeAttendanceMapper;
import com.gerp.attendance.service.excel.AttendanceExcelService;
import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
//@RequiredArgsConstructor
//@NoArgsConstructor
@Service
public class AttendanceExcelServiceImpl implements AttendanceExcelService {

    @Autowired
    private EmployeeAttendanceMapper employeeAttendanceMapper;

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    private Workbook workbook;
    private Sheet sheet;
    private int rowNumber;
    private List<String> headings = new ArrayList<>();
    private List<Integer> widths = new ArrayList<>();

    public  AttendanceExcelServiceImpl() {
        this.rowNumber = 0;
        this.widths = Arrays.asList(
                10,
                40,
                40,
                20,
                40,
                40,
                40,
                40,
                40,
                80,
                80
        );
    }


            List<String>heading1 = Arrays.asList(
            "S.N",
            "Employee Name",
            "Employee Designation",
            "Date",
            "Check in",
            "Check out",
            "Late Check in",
            "Late Check out",
            "Extra Time",
            "Attendance Type",
                    "Attendance Remarks"
            );
    List<String> heading2 =Arrays.asList(
            "क्र.सं",
            "कर्मचारीको नाम",
            "पद",
            "मिति",
            "चेक इन",
            "चेक आउट",
            "ढिलो चेकइन",
            "प्रारम्भिक चेकआउट",
            "अतिरिक्त समय",
            "हाजिरी प्रकार",
            "हाजिरी टिप्पणी"
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
        this.sheet = this.workbook.createSheet(reportPojo.getFromDate() + " - " + reportPojo.getToDate());
        this.setHeadingAndWidth();
        CellStyle subCellStyleEnd = this.getCommonStyle();
        subCellStyleEnd.setAlignment(HorizontalAlignment.RIGHT);

        List<EmployeeAttendanceReportDataPojo> data = employeeAttendanceMapper.filterData(
                reportPojo.getPisCode(),
                AttendanceStatus.DEVICE.toString(),
                AttendanceStatus.MA.toString(),
                reportPojo.getFromDate(),
                reportPojo.getToDate(),
                reportPojo.getSearchField()
        );

        data.stream().forEach(x->{
            if(reportPojo.getPisCode()!=null && !reportPojo.getPisCode().equals("")) {
                EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(reportPojo.getPisCode());
                x.setEmployeeNameEn(pis.getEmployeeNameEn());
                x.setEmployeeNameNp(pis.getEmployeeNameNp());
                x.setEmployeeDesignationEn(pis.getEmployeeNameEn());
                x.setEmployeeDesignationNp(pis.getEmployeeNameNp());
            }
        });




        int p = 1;
            for (EmployeeAttendanceReportDataPojo y : data) {
                Row row = sheet.createRow(rowNumber++);

                int q = 0;

                this.createCell(row, q++, p, this.getCommonStyle());
                if(reportType==0) {
                    this.createCell(row, q++, y.getEmployeeNameEn(), this.getCommonStyle());
                    this.createCell(row, q++, y.getEmployeeDesignationEn(), this.getCommonStyle());
                }else{
                    this.createCell(row, q++, y.getEmployeeNameNp(), this.getCommonStyle());
                    this.createCell(row, q++, y.getEmployeeDesignationNp(), this.getCommonStyle());
                }
                this.createCell(row, q++, y.getDates().toString(), this.getCommonStyle());
                this.createCell(row, q++, y.getCheckin()==null ? null: y.getCheckin().toString(), this.getCommonStyle());
                this.createCell(row, q++, y.getCheckout()==null ? null:y.getCheckout().toString(), this.getCommonStyle());
                this.createCell(row, q++, y.getLateCheckin() == null ? null : y.getLateCheckin().toString(), this.getCommonStyle());


                this.createCell(row, q++, y.getEarlyCheckout() == null ? null : y.getEarlyCheckout().toString(), this.getCommonStyle());
                this.createCell(row, q++, y.getExtraTime() == null ? null : y.getExtraTime().toString(), this.getCommonStyle());
                if(reportType==0) {
                    this.createCell(row, q++, y.getAttendanceType() == null ? null : y.getAttendanceType().getEnum().getValueEnglish(), this.getCommonStyle());
                    this.createCell(row, q++, y.getAttendanceRemarks() == null ? null : y.getAttendanceRemarks().getEnum().getValueEnglish(), this.getCommonStyle());
                }else{
                    this.createCell(row, q++, y.getAttendanceType() == null ? null : y.getAttendanceType().getEnum().getValueNepali(), this.getCommonStyle());
                    this.createCell(row, q++, y.getAttendanceRemarks() == null ? null : y.getAttendanceRemarks().getEnum().getValueNepali(), this.getCommonStyle());
                }
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
