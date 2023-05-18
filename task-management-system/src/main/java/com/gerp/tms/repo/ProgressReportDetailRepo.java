package com.gerp.tms.repo;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.tms.model.report.ProgressReportDetail;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressReportDetailRepo extends GenericSoftDeleteRepository<ProgressReportDetail,Integer> {
    ProgressReportDetail findByTaskIdAndCreatedBy(Long taskId,Long createdBy );
}
