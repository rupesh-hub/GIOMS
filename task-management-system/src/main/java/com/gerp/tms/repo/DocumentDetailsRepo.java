package com.gerp.tms.repo;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.tms.model.project.ProjectDocumentDetails;
import org.springframework.stereotype.Repository;

/**
 * @author Jitesh Nemkul
 */
@Repository
public interface DocumentDetailsRepo extends GenericSoftDeleteRepository<ProjectDocumentDetails, Integer> {
}
