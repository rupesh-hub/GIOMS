package com.gerp.usermgmt.mapper.usermgmt;
import com.gerp.usermgmt.model.UserRoleLog;
import com.gerp.usermgmt.pojo.RoleLogResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.management.relation.Role;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserRoleLogMapper {

    List<RoleLogResponsePojo> findRoleHistory(@Param("pisCode") String pisCode, @Param("fiscalYearStartDate") LocalDate fiscalYearStartDate, @Param("fiscalYearEndDate") LocalDate fiscalYearEndDate);

    UserRoleLog getLatestUserRoleLogByUserId(@Param("userId") Long userId);

}
