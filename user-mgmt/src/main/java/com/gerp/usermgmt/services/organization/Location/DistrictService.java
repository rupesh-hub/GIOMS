package com.gerp.usermgmt.services.organization.Location;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.address.District;

import java.util.List;

public interface DistrictService extends GenericService<District, String> {

    List<District> findAllByProvinceId(Integer pId);

    List<IdNamePojo> findAllDistricts();

    IdNamePojo findByIdMinimal(String code);
}
