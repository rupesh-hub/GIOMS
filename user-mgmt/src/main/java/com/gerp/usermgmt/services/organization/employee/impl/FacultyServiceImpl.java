package com.gerp.usermgmt.services.organization.employee.impl;

import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.mapper.organization.FacultyMapper;
import com.gerp.usermgmt.model.employee.Faculty;
import com.gerp.usermgmt.repo.employee.FacultyRepo;
import com.gerp.usermgmt.services.organization.employee.FacultyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultyServiceImpl  extends GenericServiceImpl<Faculty, String> implements FacultyService {
    private final FacultyRepo facultyRepo;
    private final FacultyMapper facultyMapper;

    public FacultyServiceImpl(FacultyRepo facultyRepo, FacultyMapper facultyMapper) {
        super(facultyRepo);
        this.facultyRepo = facultyRepo;
        this.facultyMapper = facultyMapper;
    }

    @Override
    public List<IdNamePojo> facultyMinimal() {
        return facultyRepo.getALlFaculties();
    }

    @Override
    public List<IdNamePojo> facultyByEducationLevel(String educationLevelCode) {
        return facultyMapper.facultyByEducationLevelCode(educationLevelCode);
    }
}
