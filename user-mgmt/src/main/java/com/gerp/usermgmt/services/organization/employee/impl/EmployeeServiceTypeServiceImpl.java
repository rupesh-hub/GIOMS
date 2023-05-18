package com.gerp.usermgmt.services.organization.employee.impl;

import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.model.employee.EmployeeServiceType;
import com.gerp.usermgmt.repo.employee.EmployeeServiceTypeRepo;
import com.gerp.usermgmt.services.organization.employee.EmployeeServiceTypeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceTypeServiceImpl extends GenericServiceImpl<EmployeeServiceType, String> implements EmployeeServiceTypeService {
    private final EmployeeServiceTypeRepo employeeServiceTypeRepo;

    public EmployeeServiceTypeServiceImpl(EmployeeServiceTypeRepo employeeServiceTypeRepo) {
        super(employeeServiceTypeRepo);
        this.employeeServiceTypeRepo = employeeServiceTypeRepo;
    }
}
