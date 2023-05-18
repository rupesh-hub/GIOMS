package com.gerp.attendance.service.excel.impl;

import com.gerp.attendance.Pojo.DailyInformationPojo;
import com.gerp.attendance.Pojo.KaajRequestCustomPojo;
import com.gerp.attendance.Pojo.report.ReportPojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.EmployeeAttendanceMapper;
import com.gerp.attendance.service.excel.DailyInformationExcelService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DailyInformationExcelServiceImpl implements DailyInformationExcelService {
    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private EmployeeAttendanceMapper employeeAttendanceMapper;

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    private Workbook workbook;
    private Sheet sheet;
    private int rowNumber;
    private List<String> headings = new ArrayList<>();
    private List<Integer> widths = new ArrayList<>();

    public DailyInformationExcelServiceImpl(){
        this.rowNumber = 0;
        this.widths = Arrays.asList(
                5,
                10,
                30,
                20,
                10,
                10,
                10,
                10,
                10,
                30

        );
    }

    List<String>heading1 = Arrays.asList(
            "S.N",
            "Pis Number",
            "Employee Name",
            "Designation",
            "Date",
            "Open Time",
            "Close Time",
            "In Time",
            "out Time",
            "Status"
    );
    List<String>heading2 = Arrays.asList(
            "क्र.सं",
            "पीआईएस कोड",
            "कर्मचारीको नाम",
            "पद",
            "मिति",
            "खुल्ने समय",
            "बन्दहुने समय",
            "प्रवेश समय",
            "बाहिरिएको समय",
            "स्थिति"
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
        this.sheet = this.workbook.createSheet(reportPojo.getDate().toString());
        this.setHeadingAndWidth();
        CellStyle subCellStyleEnd = this.getCommonStyle();
        subCellStyleEnd.setAlignment(HorizontalAlignment.RIGHT);

         List<DailyInformationPojo> dailyInformationPojos = employeeAttendanceMapper.getFilterDailyInformation(
                 reportPojo.getFiscalYear().toString(),
                 reportPojo.getOfficeCode(),
                 reportPojo.getDate(),
                 reportPojo.getSearchField()
         );


        int p=1;
        for (DailyInformationPojo y : dailyInformationPojos) {
            if (y.getPisCode() != null && !y.getPisCode().equals("")) {
                EmployeeMinimalPojo pis = userMgmtServiceData.getEmployeeDetailMinimal(y.getPisCode());
                y.setEmployeeDetails(pis);
            }

            Row row = sheet.createRow(rowNumber++);

            int q =0;

            this.createCell(row ,q++, p, this.getCommonStyle());
            this.createCell(row ,q++, y.getPisCode(), this.getCommonStyle());
            if(reportType==0) {
                this.createCell(row, q++, y.getEmpNameEn(), this.getCommonStyle());
                this.createCell(row, q++, y.getFdNameEn(), this.getCommonStyle());
            }else{
                this.createCell(row, q++, y.getEmpNameNp(), this.getCommonStyle());
                this.createCell(row, q++, y.getFdNameNp(), this.getCommonStyle());
            }
            this.createCell(row ,q++, y.getDateNp().toString(), this.getCommonStyle());
            this.createCell(row ,q++, y.getOpenTime().toString(), this.getCommonStyle());
            this.createCell(row ,q++, y.getCloseTime().toString(), this.getCommonStyle());
            this.createCell(row ,q++, y.getInTime()==null? null:y.getInTime().toString(), this.getCommonStyle());
            this.createCell(row ,q++, y.getOutTime()==null?null:y.getOutTime().toString(), this.getCommonStyle());
            if(reportType==0) {
                this.createCell(row ,q++, y.getStatus().getEnum().getValueEnglish(), this.getCommonStyle());
            }else{
                this.createCell(row ,q++, y.getStatus().getEnum().getValueNepali(), this.getCommonStyle());
            }

            p++;
        }
        return this.workbook;
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
}
