package com.gerp.attendance.service.excel;

import com.gerp.attendance.Pojo.report.ReportPojo;
import org.apache.poi.ss.usermodel.Workbook;

public interface KaajRequestExcelService {

    Workbook loadDataToSheet(ReportPojo reportPojo);

}
