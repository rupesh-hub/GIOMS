package com.gerp.attendance.service.excel;

import com.gerp.attendance.Pojo.report.ReportPojo;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MonthlyAttendanceExcelService {

    Workbook loadDataToSheet(ReportPojo reportPojo);
}
