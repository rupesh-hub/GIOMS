package com.gerp.usermgmt.services.usermgmt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.UserRoleLog;
import com.gerp.usermgmt.pojo.RoleLogDetailPojo;
import com.gerp.usermgmt.pojo.RoleLogResponsePojo;

import java.util.List;

public interface UserRoleLogService extends GenericService<UserRoleLog, Long> {


    List<RoleLogResponsePojo> getRoleHistory(String pisCode, String fiscalYearCode) throws JsonProcessingException;

    List<Long> findAllLatestChangedRoleIds(Long userId);
}
