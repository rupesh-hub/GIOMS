package com.gerp.usermgmt.repo.delegation;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.delegation.DelegationLog;

public interface DelegationLogRepository extends GenericRepository<DelegationLog, Long> {

    DelegationLog findFirstByDelegationIdOrderByCreatedDate(Integer delegationId);
}
