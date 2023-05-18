package com.gerp.usermgmt.services.organization.employee;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.employee.Faculty;

import java.util.List;

public interface FacultyService extends GenericService<Faculty, String> {
    List<IdNamePojo> facultyMinimal();
    List<IdNamePojo> facultyByEducationLevel(String educationLevelCode);
}
