package com.gerp.usermgmt.validator;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.shared.utils.HelperUtil;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.model.employee.Position;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.repo.designation.FunctionalDesignationRepo;
import com.gerp.usermgmt.repo.designation.PositionRepo;
import com.gerp.usermgmt.repo.employee.EmployeeRepo;
import com.gerp.usermgmt.repo.employee.ServiceGroupRepo;
import com.gerp.usermgmt.repo.office.OfficeRepo;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EntityValidator {

    private final PositionRepo positionRepo;
    private final ServiceGroupRepo serviceGroupRepo;
    private final OfficeRepo officeRepo;
    private final FunctionalDesignationRepo designationRepo;
    private final CustomMessageSource customMessageSource;
    private final EmployeeRepo employeeRepo;

    private final String position = "Position";
    private final String service = "Service";
    private final String designation = "Designation";
    private final String office = "Office";
    private final String employee = "Employee";

    public EntityValidator(PositionRepo positionRepo, ServiceGroupRepo serviceGroupRepo, OfficeRepo officeRepo, FunctionalDesignationRepo designationRepo, CustomMessageSource customMessageSource, EmployeeRepo employeeRepo) {
        this.positionRepo = positionRepo;
        this.serviceGroupRepo = serviceGroupRepo;
        this.officeRepo = officeRepo;
        this.designationRepo = designationRepo;
        this.customMessageSource = customMessageSource;
        this.employeeRepo = employeeRepo;
    }

    public void validateEntities(String positionCode, String serviceCode, String designationCode, String officeCode) {
        this.validateEntities(positionCode,serviceCode,designationCode);
        getOffice(officeCode);
    }
    public void validateEntities(String positionCode, String serviceCode, String designationCode) {
        getPosition(positionCode);
        getService(serviceCode);
        getDesignation(designationCode);
    }

    public Office getOffice(String officeCode) {
        Optional<Office> officeOptional = officeRepo.findById(officeCode);
        if (!officeOptional.isPresent()) {
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist, office));
        }
        return officeOptional.orElse(new Office());
    }

    public FunctionalDesignation getDesignation(String designationCode) {
        Optional<FunctionalDesignation> functionalDesignationOptional = designationRepo.findById(designationCode);
        if (!functionalDesignationOptional.isPresent()) {
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist, designation));
        }
        return functionalDesignationOptional.orElse(new FunctionalDesignation());

    }

    public Employee getEmployee(String employeeCode) {
        Optional<Employee> employeeOptional = employeeRepo.findById(employeeCode);
        if (!employeeOptional.isPresent()) {
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist, employee));
        }
        return employeeOptional.orElse(new Employee());
    }

    public com.gerp.usermgmt.model.employee.Service getService(String serviceCode) {
        Optional<com.gerp.usermgmt.model.employee.Service> serviceOptional = serviceGroupRepo.findById(serviceCode);
        if (!serviceOptional.isPresent()) {
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist, service));
        }
        return serviceOptional.orElse(new com.gerp.usermgmt.model.employee.Service());
    }

    public Position getPosition(String positionCode) {
        Optional<Position> positionOptional = positionRepo.findById(positionCode);
        if (!positionOptional.isPresent()) {
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist, position));
        }
        return positionOptional.orElse(new Position());
    }
}
