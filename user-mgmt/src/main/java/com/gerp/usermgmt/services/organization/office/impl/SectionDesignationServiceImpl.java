package com.gerp.usermgmt.services.organization.office.impl;


import com.gerp.shared.constants.cacheconstants.EmpCacheConst;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.Proxy.DartaChalaniServiceData;
import com.gerp.usermgmt.converter.organiztion.office.SectionDesignationConverter;
import com.gerp.usermgmt.mapper.organization.SectionDesignationMapper;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.model.employee.EmployeeSectionDesignationLog;
import com.gerp.usermgmt.model.employee.SectionDesignation;
import com.gerp.usermgmt.pojo.darta.SectionInvolvedPojo;
import com.gerp.usermgmt.pojo.organization.office.SectionDesignationPojo;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;
import com.gerp.usermgmt.repo.employee.EmployeeRepo;
import com.gerp.usermgmt.repo.employee.EmployeeSectionDesignationLogRepo;
import com.gerp.usermgmt.repo.office.SectionDesignationRepo;
import com.gerp.usermgmt.services.organization.employee.EmployeeSectionDesignationLogService;
import com.gerp.usermgmt.services.organization.employee.EmployeeService;
import com.gerp.usermgmt.services.organization.employee.impl.ServiceGroupServiceImpl;
import com.gerp.usermgmt.services.organization.office.SectionDesignationService;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
public class SectionDesignationServiceImpl extends GenericServiceImpl<SectionDesignation , Integer> implements SectionDesignationService{
    private final SectionDesignationRepo sectionDesignationRepo;
    private final SectionDesignationMapper sectionDesignationMapper;
    private final SectionDesignationConverter sectionDesignationConverter;
    private final EmployeeService employeeService;
    private final ServiceGroupServiceImpl serviceGroupService;

    private final EmployeeSectionDesignationLogService sectionDesignationLogService;
    private final EmployeeRepo employeeRepo;
    private final EmployeeSectionDesignationLogRepo employeeSectionDesignationLogRepo;

    private final DartaChalaniServiceData dartaChalaniServiceData;

    public SectionDesignationServiceImpl(SectionDesignationRepo sectionDesignationRepo, SectionDesignationMapper sectionDesignationMapper, SectionDesignationConverter sectionDesignationConverter, EmployeeService employeeService, ServiceGroupServiceImpl serviceGroupService, EmployeeSectionDesignationLogService sectionDesignationLogService, EmployeeRepo employeeRepo,
                                         EmployeeSectionDesignationLogRepo employeeSectionDesignationLogRepo,
                                         DartaChalaniServiceData dartaChalaniServiceData) {
        super(sectionDesignationRepo);
        this.sectionDesignationRepo = sectionDesignationRepo;
        this.sectionDesignationMapper = sectionDesignationMapper;
        this.sectionDesignationConverter = sectionDesignationConverter;
        this.employeeService = employeeService;
        this.serviceGroupService = serviceGroupService;
        this.sectionDesignationLogService = sectionDesignationLogService;
        this.employeeRepo = employeeRepo;
        this.employeeSectionDesignationLogRepo = employeeSectionDesignationLogRepo;
        this.dartaChalaniServiceData = dartaChalaniServiceData;
    }

    @Override
    public List<Integer> saveByVacancy(SectionDesignationPojo sectionDesignationPojo) {
        SectionDesignation sectionDesignation = sectionDesignationConverter.toEntity(sectionDesignationPojo);
        if (sectionDesignationPojo.getNoOfVacancy() != null && sectionDesignationPojo.getNoOfVacancy() != 0 ){
            List<SectionDesignation> sectionDesignationList = new ArrayList<>();
            for (int i=0; i < sectionDesignationPojo.getNoOfVacancy();i++){
                SectionDesignation sectionDesignationTempo =new SectionDesignation(sectionDesignation);
                sectionDesignationList.add(sectionDesignationTempo);
            }
            return sectionDesignationRepo.saveAll(sectionDesignationList).stream().map(SectionDesignation::getId).collect(Collectors.toList());
        }
        List<Integer> returnId = new ArrayList<>();
        returnId.add(sectionDesignationRepo.save(sectionDesignation).getId());
        return returnId;
    }

    @Override
    public Integer save(SectionDesignationPojo sectionDesignationPojo) {
        SectionDesignation sectionDesignation = sectionDesignationConverter.toEntity(sectionDesignationPojo);
        return sectionDesignationRepo.save(sectionDesignation).getId();
    }

    @Override
    public List<Integer> saveAll(List<SectionDesignationPojo> sectionDesignationPojo) {
        List<SectionDesignation> sectionDesignations= sectionDesignationConverter.toEntity(sectionDesignationPojo);
        return sectionDesignationRepo.saveAll(sectionDesignations).stream().map(SectionDesignation::getId).collect(Collectors.toList());
    }

    @Override
    public Integer update(SectionDesignationPojo sectionDesignationPojo) {
        SectionDesignation sectionDesignation = sectionDesignationRepo.findById(sectionDesignationPojo.getId()).orElse(null);
        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(sectionDesignation, sectionDesignationConverter.toEntity(sectionDesignationPojo));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("id doesnot exists");
        }
        assert sectionDesignation != null;
        return sectionDesignationRepo.save(sectionDesignation).getId();
    }

    @Override
    public SectionDesignationPojo findSectionDesignationById(Integer id) {
        SectionDesignationPojo s = sectionDesignationMapper.getSectionDesignationAllDetailById(id);
        if(s.getEmployee() != null && s.getEmployee().getPisCode() == null) {
            s.setEmployee(null);
        }

      // in case of special designation there is no service
        if(s.getService() !=null && s.getService().getCode() !=null) {
            Map<String, Object> map = serviceGroupService.findServiceIdWithHierarchy(s.getService().getCode());
            s.setService(null);
            s.setGroup(null);
            s.setSubGroup(null);
            s.setService((ServicePojo) map.get(StringConstants.SERVICE));
            s.setGroup((ServicePojo) map.get(StringConstants.GROUP));
            s.setSubGroup((ServicePojo) map.get(StringConstants.SUBGROUP));
        }



        s.setEmployeeAssignLog(sectionDesignationLogService.getDesignationAssignLog(id));
        return s;
    }

    @Override
    public List<SectionDesignationPojo> findSectionDesignationByEmployee(String pisCode) {
        return sectionDesignationMapper.getSectionDesignationByPisCode(pisCode);
    }

    @Override
    public List<SectionDesignationPojo> findSectionDesignations(Integer id) {
        return null;
    }

    @Override
    @CacheEvict(cacheNames = {EmpCacheConst.CACHE_VALUE_MINIMAL ,
            EmpCacheConst.CACHE_VALUE_DETAIL,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_MINIMAL_EATT,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_DETAIL_EATT}, key = "#sectionDesignationPojo.getPisCode()", condition = "#result != null ")
    public Integer assignEmployee(SectionDesignationPojo sectionDesignationPojo) {
        SectionDesignation sectionDesignation = sectionDesignationRepo.getOne(sectionDesignationPojo.getId());
        Employee employee = employeeService.detail(sectionDesignationPojo.getPisCode());
        if(employee == null) {
            throw new NullPointerException("employee not found");
        }
        sectionDesignation.setEmployee(employee);
        sectionDesignation.setActive(sectionDesignationMapper.getSectionDesignationByPisCode(employee.getPisCode()).isEmpty());
        sectionDesignation.setOnTransferProcess(Boolean.FALSE);
//        log.info(sectionDesignation.getEmployee().getPisCode() + "hello");
        return sectionDesignationRepo.save(sectionDesignation).getId();
    }

    @Override
    public Integer getSectionDesignationIdByEmployeeSection(String pisCode, Long sectionId) {
        return sectionDesignationMapper.getSectionDesignationIdByPisCodeAndSectionId(pisCode, sectionId);
    }

    @Override
    @CacheEvict(cacheNames = {EmpCacheConst.CACHE_VALUE_MINIMAL ,
            EmpCacheConst.CACHE_VALUE_DETAIL,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_MINIMAL_EATT,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_DETAIL_EATT}, keyGenerator = "employeeCacheKeyGenerator", condition = "#result != null")
    public Integer detachEmployee(Integer id) {
        SectionDesignation sectionDesignation = sectionDesignationRepo.getOne(id);
        Employee employee = sectionDesignation.getEmployee();
        employee.setSectionSubsection(null);
        if(sectionDesignation.getEmployee() == null || sectionDesignation.getFunctionalDesignation() == null) {
            throw new RuntimeException("designation not found");
        }
        employeeRepo.save(employee);
        sectionDesignation.setEmployee(null);
        sectionDesignation.setOnTransferProcess(Boolean.FALSE);
        return sectionDesignationRepo.save(sectionDesignation).getId();
    }

    @Override
    public void deleteDesignation(Integer id) {
        SectionDesignation sectionDesignation = sectionDesignationRepo.getOne(id);
        if(ObjectUtils.isEmpty(sectionDesignation)){
            throw  new RuntimeException("Designation not found");
        }
        if(!ObjectUtils.isEmpty(sectionDesignation.getEmployee())){
            throw  new RuntimeException("Remove Employee from the designation");
        }
        //todo:  get letter based on previous employee on section designation

        String sectionCode = sectionDesignationMapper.getSectionIdFromSectionDesgnaitonId(sectionDesignation.getId());

        SectionInvolvedPojo sectionInvolvedPojo = dartaChalaniServiceData.checkLetter(sectionCode);
        if(sectionInvolvedPojo.getIsSectionInvolved()) throw new CustomException("Letter present on the section ");
        sectionDesignationRepo.deactivateSectionDesignation(sectionDesignation.getId());
    }

    @Override
    @CacheEvict(cacheNames = {EmpCacheConst.CACHE_VALUE_MINIMAL ,
            EmpCacheConst.CACHE_VALUE_DETAIL,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_MINIMAL_EATT,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_DETAIL_EATT}, keyGenerator = "employeeCacheKeyGenerator", condition = "#result = true ")
    public Boolean changeActiveSectionDesignation(Integer sectionDesignationId) {
        SectionDesignation sectionDesignations = sectionDesignationRepo.getSectionDesignationById(sectionDesignationId);
        if(sectionDesignations == null || sectionDesignations.getEmployee() == null){
            throw new NullPointerException("No employee or designation found");
        }
        sectionDesignations.setActive(true);
        sectionDesignationRepo.save(sectionDesignations);
        sectionDesignationRepo.inActiveSectionDesignation(sectionDesignationId, sectionDesignations.getEmployee().getPisCode());
        return true;
    }

    @Override
    public void updateTransferProcess(Boolean isOnTransferProcess, String pisCode) {
        sectionDesignationRepo.updateTransferProcess(pisCode, isOnTransferProcess);
    }

    @Override
    @CacheEvict(value = EmpCacheConst.CACHE_VALUE_EMPLOYEE_SECTION, key = "#result.getEmployee().pisCode+'_'+#result.getSectionSubsection().getId()", condition = "#result.getEmployee() != null")
    public SectionDesignation findPreviousSectionDesignation(Integer id) {
        SectionDesignation sectionDesignation = this.findById(id);
        // creating the new object to send for previous section record
        // need only section id and the previous employee
        SectionDesignation sectionDesignation1 = new SectionDesignation();
        System.out.println(sectionDesignation.getSectionSubsection().getId());
        sectionDesignation1.setId(sectionDesignation.getId());
        sectionDesignation1.setSectionSubsection(sectionDesignation.getSectionSubsection());
        EmployeeSectionDesignationLog employeeSectionDesignationLog = employeeSectionDesignationLogRepo.findLatestLogRecord(id);
        System.out.println(sectionDesignation1.getSectionSubsection().getId());
        System.out.println(sectionDesignation1.getEmployee());
        if(employeeSectionDesignationLog !=null && employeeSectionDesignationLog.getNewEmployeePisCode() !=null){
            sectionDesignation1.setEmployee(employeeRepo.findByPisCode(employeeSectionDesignationLog.getNewEmployeePisCode()));
            System.out.println(sectionDesignation1.getEmployee().getPisCode());
            return sectionDesignation1;
        }
        // setting the previous employee to null if there is not any log present for previous designation history
        sectionDesignation1.setEmployee(null);
        return sectionDesignation1;
    }
}
