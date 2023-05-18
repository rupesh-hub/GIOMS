package com.gerp.usermgmt.aspects;

import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.model.employee.EmployeeJobDetailLog;
import com.gerp.usermgmt.pojo.organization.employee.EmployeePromotionPojo;
import com.gerp.usermgmt.services.organization.jobdetail.EmployeeJobDetailService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class EmployeePromotionLogAspect {
    private final EmployeeJobDetailService employeeJobDetailService;
    private final DateConverter dateConverter;
    private final TokenProcessorService tokenProcessorService;

        public EmployeePromotionLogAspect(EmployeeJobDetailService employeeJobDetailService, DateConverter dateConverter, TokenProcessorService tokenProcessorService) {
        this.employeeJobDetailService = employeeJobDetailService;
        this.dateConverter = dateConverter;
            this.tokenProcessorService = tokenProcessorService;
        }

    @Transactional
    public void logAction(Employee employee , EmployeePromotionPojo employeePromotionPojo) {
        this.updatePreviousLog(employee.getPisCode());
        EmployeeJobDetailLog ejl = new EmployeeJobDetailLog();
        ejl.setPisCode(employee.getPisCode());
        ejl.setPromotedOfficeCode(tokenProcessorService.getOfficeCode());
        ejl.setIsPromotion(employeePromotionPojo.getIsPromotion());

        ejl.setOldDesignationCode(employee.getDesignation().getCode());
        ejl.setOldPositionCode(employee.getPosition().getCode());
        ejl.setOldServiceCode(employee.getService().getCode());

        ejl.setStartDate(LocalDate.now());
        ejl.setStartDateNp(dateConverter.convertAdToBs(String.valueOf(LocalDate.now())));

        ejl.setNewDesignationCode(employeePromotionPojo.getFunctionalDesignation().getCode());
        ejl.setNewPositionCode(employeePromotionPojo.getPosition().getCode());
        ejl.setNewServiceCode(employeePromotionPojo.getService().getCode());

        ejl.setIsPeriodOver(Boolean.FALSE);
        ejl.setActive(Boolean.TRUE);
        employeeJobDetailService.create(ejl);
    }

    private void updatePreviousLog(String pisCode) {
        EmployeeJobDetailLog employeeJobDetailLog = employeeJobDetailService.getPrevActiveLog(pisCode);
        if(employeeJobDetailLog != null) {
            employeeJobDetailLog.setEndDateNp(dateConverter.convertAdToBs(String.valueOf(LocalDate.now())));
            employeeJobDetailLog.setEndDate(LocalDate.now());
            employeeJobDetailLog.setIsPeriodOver(Boolean.TRUE);
            employeeJobDetailLog.setActive(Boolean.FALSE);
            employeeJobDetailService.update(employeeJobDetailLog);
        }
    }
}
