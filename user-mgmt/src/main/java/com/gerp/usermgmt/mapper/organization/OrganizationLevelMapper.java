package com.gerp.usermgmt.mapper.organization;

import com.gerp.usermgmt.model.office.OrganizationLevel;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.organization.office.OrganizationLevelPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface OrganizationLevelMapper {

    List<OrganizationLevelPojo> getAllOfficeLevel(@Param("organizationTypeId") Long organizationTypeId);
}
