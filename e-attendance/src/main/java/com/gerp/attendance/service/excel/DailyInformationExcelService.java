package com.gerp.attendance.service.excel;

import com.gerp.attendance.Pojo.report.ReportPojo;
import org.apache.poi.ss.usermodel.Workbook;

import java.time.LocalDate;

public interface DailyInformationExcelService {
    Workbook loadDataToSheet(ReportPojo reportPojo,int reportType);
}
