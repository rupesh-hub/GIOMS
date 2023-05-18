package com.gerp.usermgmt.services.transfer.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.model.transfer.EmployeeRequestCheckList;
import com.gerp.usermgmt.pojo.transfer.EmployeeChecklistPojo;
import com.gerp.usermgmt.repo.transfer.EmployeeRequestCheckListRepo;
import com.gerp.usermgmt.services.transfer.EmployeeChecklistService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeChecklistServiceImpl implements EmployeeChecklistService {
    private final EmployeeRequestCheckListRepo employeeRequestCheckListRepo;
    private final CustomMessageSource customMessageSource;

    public EmployeeChecklistServiceImpl(EmployeeRequestCheckListRepo employeeRequestCheckListRepo, CustomMessageSource customMessageSource) {
        this.employeeRequestCheckListRepo = employeeRequestCheckListRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public int addCheckList(EmployeeChecklistPojo employeeChecklistPojo) {
        EmployeeRequestCheckList employeeRequestCheckList = new EmployeeRequestCheckList();
        employeeRequestCheckList.setName(employeeRequestCheckList.getName());
       return employeeRequestCheckListRepo.save(employeeRequestCheckList).getId();
    }

    @Override
    public List<EmployeeChecklistPojo> getCheckList() {
        return employeeRequestCheckListRepo.findAll().parallelStream().map(b->new EmployeeChecklistPojo(b.getId(),b.getName())).collect(Collectors.toList());
    }

    @Override
    public int updateCheckList(EmployeeChecklistPojo dto) {
        Optional<EmployeeRequestCheckList> employeeRequestCheckListOptional = employeeRequestCheckListRepo.findById(dto.getId());
        if (!employeeRequestCheckListOptional.isPresent()){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"Checklist"));
        }
        EmployeeRequestCheckList employeeRequestCheckList = employeeRequestCheckListOptional.orElse(new EmployeeRequestCheckList());
        employeeRequestCheckList.setName(dto.getName());
        employeeRequestCheckListRepo.save(employeeRequestCheckList);
        return dto.getId();
    }

    @Override
    public int deleteCheckList(int id) {
        employeeRequestCheckListRepo.deleteById(id);
        return id;
    }
}
