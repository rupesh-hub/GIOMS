package com.gerp.attendance.service;

import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MonthlyTemplateDetailService {
    byte[] generateMonthlyDetailPdf(GetRowsRequest paginatedRequest, String lang);

}
