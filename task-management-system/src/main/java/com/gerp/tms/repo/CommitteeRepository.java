package com.gerp.tms.repo;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.tms.model.committee.Committee;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitteeRepository extends GenericSoftDeleteRepository<Committee, Integer> {
}
