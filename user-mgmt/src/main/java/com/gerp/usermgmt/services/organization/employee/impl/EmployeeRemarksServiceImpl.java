package com.gerp.usermgmt.services.organization.employee.impl;

import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.usermgmt.model.employee.EmployeeRemarks;
import com.gerp.usermgmt.pojo.organization.employee.EmployeeRemarksPojo;
import com.gerp.usermgmt.repo.employee.EmployeeRemarksRepo;
import com.gerp.usermgmt.services.organization.employee.EmployeeRemarksService;
import com.gerp.usermgmt.services.organization.employee.EmployeeService;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.stereotype.Service;

@Service
public class EmployeeRemarksServiceImpl implements EmployeeRemarksService {
    private final EmployeeRemarksRepo employeeRemarksRepo;
    private final EmployeeService employeeService;

    public EmployeeRemarksServiceImpl(EmployeeRemarksRepo employeeRemarksRepo, EmployeeService employeeService) {
        this.employeeRemarksRepo = employeeRemarksRepo;
        this.employeeService = employeeService;
    }

    @Override
    public EmployeeRemarks save(EmployeeRemarks employeeRemarks) {
       if(employeeService.detail(employeeRemarks.getPisCode()) == null) {
           throw new NullPointerException("employee not found with provided piscode");
       }

        return employeeRemarksRepo.save(employeeRemarks);
    }

    @Override
    public EmployeeRemarks update(EmployeeRemarksPojo employeeRemarks) {
        EmployeeRemarks er = employeeRemarksRepo.findById(employeeRemarks.getId()).orElseThrow(() -> new NullPointerException("not found"));
        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(er, employeeRemarks);
        } catch (Exception e) {
            throw new RuntimeException("It does not exist");
        }
        return employeeRemarksRepo.save(er);
    }

    @Override
    public EmployeeRemarks getByPisCode(String pisCode) {
        return employeeRemarksRepo.getFirstEmployeeRemarksByPisCodeOrderByActionDateDesc(pisCode);
    }
}
