package com.gerp.usermgmt.services.organization.Location;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.address.Province;

import java.util.List;

public interface ProvinceService extends GenericService<Province, Integer> {
    List<Province> findAllByCountryId(Integer cId);
}
