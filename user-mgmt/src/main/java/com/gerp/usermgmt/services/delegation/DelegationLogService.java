package com.gerp.usermgmt.services.delegation;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.delegation.DelegationLog;
import com.gerp.usermgmt.pojo.delegation.DelegationLogPojo;

import java.util.List;

public interface DelegationLogService  {

    DelegationLog getLatestDelegation(Integer delegationId);


}
