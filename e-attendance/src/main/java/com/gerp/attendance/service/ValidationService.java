package com.gerp.attendance.service;

import java.time.LocalDate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ValidationService {

    void validateRequest(LocalDate fromDate, LocalDate toDate, String pisCode, String requestFor, String officeCode,Long leaveKaajId,String year, boolean isAppliedForOthers);
}
