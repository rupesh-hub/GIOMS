package com.gerp.attendance.service.excel.impl;

import com.gerp.attendance.Pojo.KaajRequestCustomPojo;
import com.gerp.attendance.Pojo.report.ReportPojo;
import com.gerp.attendance.mapper.KaajRequestMapper;
import com.gerp.attendance.service.excel.KaajRequestExcelService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class KaajRequestExcelServiceImpl implements KaajRequestExcelService {


    @Autowired private KaajRequestMapper kaajRequestMapper;

    private Workbook workbook;
    private Sheet sheet;
    private int rowNumber;
    private List<String> headings = new ArrayList<>();
    private List<Integer> widths = new ArrayList<>();

    public KaajRequestExcelServiceImpl(){
        this.rowNumber = 0;
        this.headings = Arrays.asList(
                "S.N",
                "Pis Code",
                "Kaaj Duration",
                "Kaaj Type",
                "Total Days",
                "Country",
                "Location",
                "Status"
        );
        this.widths = Arrays.asList(
                10,
                20,
                80,
                20,
                20,
                20,
                20,
                20
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

        List<KaajRequestCustomPojo> data = kaajRequestMapper.filterDataForExcel(
                reportPojo.getFiscalYear(),
                reportPojo.getPisCode(),
                reportPojo.getFromDate(),
                reportPojo.getToDate(),
                reportPojo.getApprovalStatus()==null?null:reportPojo.getApprovalStatus().toString()
        );

        int p=1;
        for (KaajRequestCustomPojo y : data) {
            Row row = sheet.createRow(rowNumber++);

            int q =0;

            this.createCell(row ,q++, p, this.getCommonStyle());
            this.createCell(row ,q++, y.getPisCode(), this.getCommonStyle());
            this.createCell(row ,q++, y.getFromDateNp()+" - "+y.getToDateNp(), this.getCommonStyle());
            this.createCell(row ,q++, y.getKaajTypeName(), this.getCommonStyle());
            this.createCell(row ,q++, String.valueOf(ChronoUnit.DAYS.between(y.getFromDateEn(), y.getToDateEn())), this.getCommonStyle());

            String country = "Nepal";
            if(y.getIsInternational()){
                country = y.getCountry().getName();
            }

            this.createCell(row ,q++, country, this.getCommonStyle());
            this.createCell(row ,q++, y.getLocation(), this.getCommonStyle());
            this.createCell(row ,q++, y.getStatus().getValueEnglish(), this.getCommonStyle());
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
