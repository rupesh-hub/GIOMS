package com.gerp.usermgmt.services.organization.Location;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.address.Country;

import java.util.List;

public interface CountryService extends GenericService<Country, String> {
List<IdNamePojo> findAllActive();
}
