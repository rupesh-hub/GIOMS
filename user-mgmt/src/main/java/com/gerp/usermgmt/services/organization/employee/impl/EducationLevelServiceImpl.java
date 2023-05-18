package com.gerp.usermgmt.services.organization.employee.impl;

import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.employee.EducationLevel;
import com.gerp.usermgmt.repo.employee.EducationLevelRepo;
import com.gerp.usermgmt.services.organization.employee.EducationLevelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EducationLevelServiceImpl extends GenericServiceImpl<EducationLevel, String> implements EducationLevelService {
    private final EducationLevelRepo educationLevelRepo;

    public EducationLevelServiceImpl(EducationLevelRepo educationLevelRepo) {
        super(educationLevelRepo);
        this.educationLevelRepo = educationLevelRepo;
    }

    @Override
    public List<IdNamePojo> getAllEducationLevelMinimalDetail() {
        return educationLevelRepo.getAllEducationLevels();
    }
}
