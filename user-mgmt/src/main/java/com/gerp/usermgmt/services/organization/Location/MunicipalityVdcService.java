package com.gerp.usermgmt.services.organization.Location;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.address.MunicipalityVdc;

import java.util.List;

public interface MunicipalityVdcService extends GenericService<MunicipalityVdc, String> {

   List<IdNamePojo> municiplityByDistrict(String districtCode);
}
