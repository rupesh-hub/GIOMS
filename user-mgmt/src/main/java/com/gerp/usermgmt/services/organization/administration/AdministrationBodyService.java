package com.gerp.usermgmt.services.organization.administration;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.administrative.AdministrativeBody;
import com.gerp.usermgmt.pojo.organization.administrative.AdministrativeBodyPojo;

import java.util.List;

public interface AdministrationBodyService extends GenericService<AdministrativeBody, Integer> {
    AdministrativeBody update(AdministrativeBodyPojo pojo);
    AdministrativeBodyPojo getById(Integer id);
    List<AdministrativeBodyPojo> findAllList();
}
