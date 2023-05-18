package com.gerp.usermgmt.repo;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.UserRoleLog;
import com.gerp.usermgmt.pojo.RoleLogDetailPojo;
import com.gerp.usermgmt.pojo.RoleLogResponsePojo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleLogRepo extends GenericRepository<UserRoleLog, Long> {


}
