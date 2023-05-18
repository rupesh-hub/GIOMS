package com.gerp.attendance.repo;

import com.gerp.attendance.model.leave.KaajRequestDocumentDetails;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DocumentDetailsRepo extends GenericSoftDeleteRepository<KaajRequestDocumentDetails, Integer> {
}
