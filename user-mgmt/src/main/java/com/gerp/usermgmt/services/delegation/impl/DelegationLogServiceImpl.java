package com.gerp.usermgmt.services.delegation.impl;

import com.gerp.usermgmt.model.delegation.DelegationLog;
import com.gerp.usermgmt.pojo.delegation.DelegationLogPojo;
import com.gerp.usermgmt.repo.delegation.DelegationLogRepository;
import com.gerp.usermgmt.services.delegation.DelegationLogService;
import com.gerp.usermgmt.services.delegation.DelegationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DelegationLogServiceImpl implements DelegationLogService {

    private final DelegationLogRepository delegationLogRepository;

    public DelegationLogServiceImpl(DelegationLogRepository delegationLogRepository) {
        this.delegationLogRepository = delegationLogRepository;
    }

    @Override
    public DelegationLog getLatestDelegation(Integer delegationId) {
        DelegationLog delegationLog = delegationLogRepository.findFirstByDelegationIdOrderByCreatedDate(delegationId);
        if(delegationLog == null || delegationLog.getId() ==null) return null;

        return delegationLog;
    }

}
