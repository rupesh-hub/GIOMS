package com.gerp.usermgmt.cache.KeyGenerator;

import com.gerp.shared.exception.CustomException;
import com.gerp.usermgmt.mapper.organization.SectionDesignationMapper;
import com.gerp.usermgmt.repo.employee.EmployeeSectionDesignationLogRepo;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Component
public class EmployeeCacheKeyGenerator implements KeyGenerator {

    private final SectionDesignationMapper sectionDesignationMapper;

    private final EmployeeSectionDesignationLogRepo employeeSectionDesignationLogRepo;

    public EmployeeCacheKeyGenerator(SectionDesignationMapper sectionDesignationMapper,
                                     EmployeeSectionDesignationLogRepo employeeSectionDesignationLogRepo) {
        this.sectionDesignationMapper = sectionDesignationMapper;
        this.employeeSectionDesignationLogRepo = employeeSectionDesignationLogRepo;
    }

    @Override
    public Object generate(Object target, Method method, Object... objects) {
        String name = method.getName();
        switch (name){
            case "changeActiveSectionDesignation":
            return generateKeyForChangeActiveSectionDesignation(objects);

            case "detachEmployee":
                return  generateKeyForDetachEmployee(objects);
            case "":


        }
        return null;
    }

    private Object generateKeyForChangeActiveSectionDesignation(Object... objects){
        String pisCode = sectionDesignationMapper.getEmployeePisCode((Integer) Arrays.stream(objects).findFirst().orElseThrow( () -> new CustomException("Section id not found")));
        if(pisCode != null){
            return pisCode;
        }
        // sending random to avoid exception when previous employee is null during cache evict;
        return "random";
    }

    private Object generateKeyForDetachEmployee(Object... objects){
        String pisCode = employeeSectionDesignationLogRepo.findPreviousEmployeePis((Integer) Arrays.stream(objects).findFirst().orElseThrow( () -> new CustomException("Section id not found")));
        if(pisCode != null){
            return pisCode;
        }
        // sending random to avoid exception when previous employee is null during cache evict;
        return "random";
    }

}
