package com.gerp.attendance.service;

import com.gerp.attendance.Pojo.shift.OfficeTimePojo;
import com.gerp.attendance.model.shift.OfficeTimeConfig;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PdfGeneratorService {
    byte[] generateMonthlyPdf(GetRowsRequest paginatedRequest, String lang);

    byte[] generateMonthlyDetailPdf(GetRowsRequest paginatedRequest, String lang);


}
