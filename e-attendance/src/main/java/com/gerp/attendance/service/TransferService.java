package com.gerp.attendance.service;

import com.gerp.shared.pojo.ApiResponsePojo;
import com.gerp.shared.pojo.GlobalApiResponse;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TransferService {
    GlobalApiResponse validatePisCodeData(String pisCode, String  targetOfficeCode);
    GlobalApiResponse approveEmployeeTransfer(String pisCode, String  targetOfficeCode);
}
