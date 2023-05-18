package com.gerp.usermgmt.services.organization.office;

import com.gerp.usermgmt.model.office.OfficeGroup;
import com.gerp.usermgmt.pojo.OfficeGroupDto;
import com.gerp.usermgmt.pojo.organization.office.ExternalOfficePojo;
import com.gerp.usermgmt.pojo.organization.office.OfficeGroupPojo;

import java.util.List;

public interface OfficeGroupingService {
    int addOfficeGroup(OfficeGroupPojo officeGroupPojo);

    List<OfficeGroupPojo> getOfficeGroup(String officeCode, String districtCode, String officeLevelCode);

    int updateOfficeGroup(OfficeGroupPojo officeGroupPojo);

    int deleteOfficeGroup(Integer id);
    OfficeGroupDto getOfficeGroup(Integer id);

    int addExternalOfficeGroup(ExternalOfficePojo officeGroupPojo);

    List<ExternalOfficePojo> getExternalOfficeGroup();

    OfficeGroup getExternalOfficeById(int parseInt);

}
