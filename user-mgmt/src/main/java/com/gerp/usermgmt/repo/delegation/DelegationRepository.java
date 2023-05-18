package com.gerp.usermgmt.repo.delegation;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.delegation.Delegation;
import org.springframework.stereotype.Repository;

@Repository
public interface DelegationRepository extends GenericSoftDeleteRepository<Delegation,Integer> {
}
