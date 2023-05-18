package com.gerp.usermgmt.services.organization.employee.impl;

import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.employee.EmployeeServiceStatus;
import com.gerp.usermgmt.repo.employee.EmployeeServiceStatusRepo;
import com.gerp.usermgmt.services.organization.employee.EmployeeServiceStatusService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceStatusServiceImpl extends GenericServiceImpl<EmployeeServiceStatus, String> implements EmployeeServiceStatusService {
    private final EmployeeServiceStatusRepo employeeServiceStatusRepo;

    public EmployeeServiceStatusServiceImpl(EmployeeServiceStatusRepo employeeServiceStatusRepo) {
        super(employeeServiceStatusRepo);
        this.employeeServiceStatusRepo = employeeServiceStatusRepo;
    }

    @Override
    public List<IdNamePojo> findAll() {
        return employeeServiceStatusRepo.findAllDto();
    }
}
