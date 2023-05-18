package com.gerp.attendance.service.impl;

import com.gerp.attendance.service.ExcelService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ExcelServiceImpl implements ExcelService {

    static String[] HEADERs = { "piscode", "checkin", "checkout", "*Note: Checkin time format example: 10:00 Checkout time format example: 17:00"};
    static String SHEET = "sample_sheet";

    @Override
    public ByteArrayInputStream load() {
        ByteArrayInputStream in = this.excekFile();
        return in;
    }

    private static ByteArrayInputStream excekFile() {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Header
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
}
