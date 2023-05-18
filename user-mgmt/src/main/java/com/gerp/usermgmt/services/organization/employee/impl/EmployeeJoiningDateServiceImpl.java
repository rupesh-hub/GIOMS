package com.gerp.usermgmt.services.organization.employee.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.usermgmt.mapper.organization.EmployeeJoiningDateMapper;
import com.gerp.usermgmt.mapper.organization.EmployeeMapper;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.model.employee.EmployeeJoiningDate;
import com.gerp.usermgmt.pojo.organization.employee.EmployeeJoiningDatePojo;
import com.gerp.usermgmt.repo.employee.EmployeeJoiningDateRepo;
import com.gerp.usermgmt.repo.employee.EmployeeRepo;
import com.gerp.usermgmt.services.organization.employee.EmployeeJoiningDateService;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeJoiningDateServiceImpl extends GenericServiceImpl<EmployeeJoiningDate, Long> implements EmployeeJoiningDateService {
    private final EmployeeJoiningDateRepo employeeJoiningDateRepo;
    private final EmployeeJoiningDateMapper employeeJoiningDateMapper;
    ObjectMapper mapper = new ObjectMapper();
    private EmployeeRepo employeeRepo;
    private EmployeeMapper employeeMapper;

    public EmployeeJoiningDateServiceImpl(EmployeeJoiningDateRepo repository, EmployeeJoiningDateRepo employeeJoiningDateRepo, EmployeeJoiningDateMapper employeeJoiningDateMapper, EmployeeRepo employeeRepo) {
        super(repository);
        this.employeeJoiningDateRepo = employeeJoiningDateRepo;
        this.employeeJoiningDateMapper = employeeJoiningDateMapper;
        this.employeeRepo = employeeRepo;
    }

    @Transactional
    @Override
    public Long save(EmployeeJoiningDatePojo employeeJoiningDatePojo) {
        Employee employee = employeeRepo.findByPisCode(employeeJoiningDatePojo.getEmployeePisCode());
        EmployeeJoiningDate employeeJoiningDate = new EmployeeJoiningDate();
        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(employeeJoiningDate, employeeJoiningDatePojo);
        } catch (Exception e) {
            throw new ServiceValidationException("Error while processing employee");
        }
        // mapping employee with joining date
        employeeJoiningDate.setEmployee(employee);

        // prev active joining date
        EmployeeJoiningDate prevActiveEmployeeDate = employeeJoiningDateMapper.getActiveJoiningDate(employeeJoiningDatePojo.getEmployeePisCode());

        if (employee.getEmployeeJoiningDates().size() > 1) {
            validateJoiningDate(employeeJoiningDatePojo);

            if (Boolean.TRUE.equals(!employeeJoiningDate.isActive()) && prevActiveEmployeeDate == null) {
                throw new ServiceValidationException("Active Joining Period is required");
            }
        }

        if (Boolean.TRUE.equals(employeeJoiningDate.isActive()) && prevActiveEmployeeDate != null) {
            prevActiveEmployeeDate.setActive(Boolean.FALSE);
            prevActiveEmployeeDate.setEmployee(employee);
            // deactivate prev active joining date
            employeeJoiningDateRepo.save(prevActiveEmployeeDate);
        }


        // add new joining date
        return employeeJoiningDateRepo.save(employeeJoiningDate).getId();
    }

    @Override
    public List<EmployeeJoiningDatePojo> getJoiningDateList(String pisCode) {
        return employeeJoiningDateMapper.getEmployeeJoiningDate(pisCode);
    }

    @Override
    @Transactional
    public void toggleJoiningDate(Long id) {
        EmployeeJoiningDate ej = employeeJoiningDateRepo.findById(id).orElseThrow(() -> new ServiceValidationException("Joining Date Not Found"));
        ej.setActive(!ej.isActive());
        EmployeeJoiningDate prevActiveEmployeeDate = employeeJoiningDateMapper.getActiveJoiningDate(ej.getEmployee().getPisCode());
       if(prevActiveEmployeeDate != null && ej.isActive()) {
           employeeJoiningDateRepo.deActivateJoiningDate(prevActiveEmployeeDate.getId());
       }
//        validateJoiningDate(ej);
        employeeJoiningDateRepo.save(ej);
    }


    private void validateJoiningDate(EmployeeJoiningDatePojo employeeJoiningDate) {
//        LocalDate maxEndDate = employeeJoiningDateMapper.maxEndDate(employeeJoiningDate.getEmployeePisCode());
//        LocalDate maxDate = employeeJoiningDateMapper.maxEndDate(employeeJoiningDate.getEmployeePisCode());
//        if (maxEndDate != null && (employeeJoiningDate.getJoinDateEn().compareTo(maxEndDate) <= 0 ||
//                employeeJoiningDate.getEndDateEn().compareTo(maxEndDate) <= 0)) {
//            throw new ServiceValidationException("Joining Date Must be Greater than Previous End Date");
//        }
//        if (maxDate != null && (employeeJoiningDate.getJoinDateEn().compareTo(maxDate) <= 0 ||
//                employeeJoiningDate.getEndDateEn().compareTo(maxDate) <= 0)) {
//            throw new ServiceValidationException("Joining Date Must be Greater than Previous End Date");
//        }

        if(employeeJoiningDateMapper.getDateAvailable(employeeJoiningDate.getEmployeePisCode() , employeeJoiningDate.getJoinDateEn() , employeeJoiningDate.getEndDateEn()) > 0) {
            throw new ServiceValidationException("Joining Date Already exists on Given Time");
        }

    }

    private void validateJoiningDate(EmployeeJoiningDate employeeJoiningDate) {
        LocalDate maxEndDate = employeeJoiningDateMapper.maxEndDate(employeeJoiningDate.getEmployee().getPisCode());
        LocalDate maxDate = employeeJoiningDateMapper.maxEndDate(employeeJoiningDate.getEmployee().getPisCode());
        if (maxEndDate != null && (employeeJoiningDate.getJoinDateEn().compareTo(maxEndDate) <= 0 ||
                employeeJoiningDate.getEndDateEn().compareTo(maxEndDate) <= 0)) {
            throw new ServiceValidationException("Active Joining Date Must be Greater than Previous End Date");
        }
        if (maxDate != null && (employeeJoiningDate.getJoinDateEn().compareTo(maxDate) <= 0 ||
                employeeJoiningDate.getEndDateEn().compareTo(maxDate) <= 0)) {
            throw new ServiceValidationException("Active Joining Date Must be Greater than Previous End Date");
        }

    }

}
