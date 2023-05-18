package com.gerp.kasamu.repo;

import com.gerp.kasamu.model.committee.Committee;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitteeRepo extends GenericSoftDeleteRepository<Committee,Integer> {
    Committee findByPisCode(String pisCode);
}
