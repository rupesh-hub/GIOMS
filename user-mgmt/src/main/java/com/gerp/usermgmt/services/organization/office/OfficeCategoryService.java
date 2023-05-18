package com.gerp.usermgmt.services.organization.office;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.office.OfficeCategory;

import java.util.List;

public interface OfficeCategoryService extends GenericService<OfficeCategory, String> {

    List<OfficeCategory> getAll();
}
