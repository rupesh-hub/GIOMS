package com.gerp.usermgmt.converter.organiztion.employee;

import com.gerp.shared.generic.api.converters.AbstractConverter;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.utils.Name;
import com.gerp.shared.utils.StringDataUtils;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.model.employee.EmployeeServiceStatus;
import com.gerp.usermgmt.pojo.organization.employee.EmployeeExcelPojo;
import com.gerp.usermgmt.pojo.organization.employee.EmployeeJoiningDatePojo;
import com.gerp.usermgmt.pojo.organization.employee.EmployeePojo;
import com.gerp.usermgmt.pojo.organization.employee.KararEmployeePojo;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeConverter extends AbstractConverter<EmployeePojo, Employee> {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TokenProcessorService tokenProcessorService;

    @Override
    public Employee toEntity(EmployeePojo dto) {
        Employee employee = new Employee();
        Name nameEn = new Name(dto.getNameEn());
        Name nameNp = new Name(dto.getNameNp());
        employee.setFirstNameEn(nameEn.getFirstName());
        employee.setMiddleNameEn(nameEn.getMiddleName());
        employee.setLastNameEn(nameEn.getLastName());
        employee.setFirstNameNp(nameNp.getFirstName());
        employee.setMiddleNameNp(nameNp.getMiddleName());
        employee.setLastNameNp(nameNp.getLastName());
        return employee;
    }

    public Employee toEntity(EmployeeExcelPojo dto) {
        if(dto.getOfficeCode() == null) {
            dto.setOfficeCode(tokenProcessorService.getOfficeCode());
        }
        Employee employee = new Employee();
        modelMapper.map(dto, employee);

        return employee;
    }

    public Employee toKararEmployeeEntity(KararEmployeePojo dto){
        Employee employee = new Employee();
        modelMapper.map(dto , employee);
        employee.setIsActive(true);

        if(dto.getEmployeeServiceStatusCode() !=  null) {
            employee.setEmployeeServiceStatus(new EmployeeServiceStatus(dto.getEmployeeServiceStatusCode()));
        }
        return employee;
    }

    public KararEmployeePojo toKararDto(Employee entity) {
        KararEmployeePojo employeePojo = new KararEmployeePojo();
        modelMapper.map(entity, employeePojo);
        List<EmployeeJoiningDatePojo> employeeJoiningDate = new ArrayList<>();
        if(entity.getEmployeeJoiningDates() != null) {
            entity.getEmployeeJoiningDates().forEach(e1 -> {
                EmployeeJoiningDatePojo ep = new EmployeeJoiningDatePojo();
                BeanUtils.copyProperties(e1 , ep);
                ep.setIsActive(e1.getActive());
                employeeJoiningDate.add(ep);
            });
        }
        employeePojo.setEmployeeJoiningDates(employeeJoiningDate);
        return employeePojo;
    }

    @Override
    public EmployeePojo toDto(Employee entity) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        EmployeePojo employeePojo = new EmployeePojo();
        modelMapper.map(entity, employeePojo);
        employeePojo.setNameEn(StringDataUtils.concatName(entity.getFirstNameEn(), entity.getMiddleNameEn(), entity.getLastNameEn()));
        employeePojo.setNameNp(StringDataUtils.concatName(entity.getFirstNameNp(), entity.getMiddleNameNp(), entity.getLastNameNp()));
        employeePojo.setPisCode(entity.getPisCode());
        employeePojo.setPositionCode(entity.getPosition().getCode());
        employeePojo.setGender(entity.getGender());
        employeePojo.setEmailAddress(entity.getEmailAddress());
        employeePojo.setMobileNumber(entity.getMobileNumber());
        employeePojo.setOfficeCode(entity.getOffice().getCode());
        employeePojo.setIsActive(entity.getIsActive());
        employeePojo.setCurrentPositionAppDateAd(entity.getCurrentPositionAppDateAd());
        employeePojo.setCurrentPositionAppDateBs(entity.getCurrentPositionAppDateBs());
        employeePojo.setCitizenshipNumber(entity.getCitizenshipNumber());
        if (!ObjectUtils.isEmpty(entity.getDesignation())) {
            employeePojo.setFunctionalDesignation(new IdNamePojo(entity.getDesignation().getCode(),
                    entity.getDesignation().getNameEn(), entity.getDesignation().getNameNp()));
        }
        return employeePojo;
    }

    @Override
    public List<Employee> toEntity(List<EmployeePojo> dtoList) {
        return super.toEntity(dtoList);
    }

    @Override
    public List<EmployeePojo> toDto(List<Employee> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }
}
