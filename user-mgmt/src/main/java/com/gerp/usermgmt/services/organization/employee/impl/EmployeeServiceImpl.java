package com.gerp.usermgmt.services.organization.employee.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.constants.cacheconstants.EmpCacheConst;
import com.gerp.shared.enums.Privilege;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.pojo.employee.EmployeeSectionPojo;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import com.gerp.shared.utils.StringConstants;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.gerp.usermgmt.Proxy.AttendanceServiceData;
import com.gerp.usermgmt.aspects.EmployeePromotionLogAspect;
import com.gerp.usermgmt.config.DefaultPasswordEncoderFactories;
import com.gerp.usermgmt.config.generator.EmployeeServiceStatusConstant;
import com.gerp.usermgmt.converter.organiztion.employee.EmployeeConverter;
import com.gerp.usermgmt.enums.DesignationType;
import com.gerp.usermgmt.enums.ModuleKeyEnum;
import com.gerp.usermgmt.mapper.organization.EmployeeMapper;
import com.gerp.usermgmt.mapper.organization.OfficeMapper;
import com.gerp.usermgmt.mapper.organization.SectionMapper;
import com.gerp.usermgmt.mapper.transfer.TransferMapper;
import com.gerp.usermgmt.model.RoleGroup;
import com.gerp.usermgmt.model.User;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.model.employee.EmployeeServiceStatus;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.model.orgtransfer.OrgTransferRequest;
import com.gerp.usermgmt.pojo.external.TMSClientDetailRequestPojo;
import com.gerp.usermgmt.pojo.external.TMSEmployeePojo;
import com.gerp.usermgmt.pojo.organization.SearchPojo;
import com.gerp.usermgmt.pojo.organization.employee.*;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;
import com.gerp.usermgmt.repo.RoleGroupRepo;
import com.gerp.usermgmt.repo.auth.UserRepo;
import com.gerp.usermgmt.repo.employee.EmployeeRepo;
import com.gerp.usermgmt.repo.office.OfficeRepo;
import com.gerp.usermgmt.repo.office.SectionDesignationRepo;
import com.gerp.usermgmt.services.organization.designation.PositionService;
import com.gerp.usermgmt.services.organization.employee.EmployeeService;
import com.gerp.usermgmt.services.organization.employee.ServiceGroupService;
import com.gerp.usermgmt.token.TokenProcessorService;
import com.gerp.usermgmt.util.CopyObjetUtils;
import com.gerp.usermgmt.util.ExcelUploadUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    CustomMessageSource customMessageSource;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EmployeeRepo employeeRepo;
    private final OfficeRepo officeRepo;
    private final UserRepo userRepo;
    private final EmployeeConverter employeeConverter;
    private final EmployeeMapper employeeMapper;
    private final SectionMapper sectionMapper;
    private final SectionDesignationRepo sectionDesignationRepo;
    private final PositionService positionService;
    private final ServiceGroupService serviceGroupService;
    private final TransferMapper transferMapper;
    private final AttendanceServiceData attendanceServiceData;

    private final RoleGroupRepo roleGroupRepo;
    private final DateConverter dateConverter;


    @Autowired
    private OfficeMapper officeMapper;
    @Autowired
    private TokenProcessorService tokenProcessorService;
    @Autowired
    private EmployeePromotionLogAspect employeePromotionLogAspect;

    @Autowired
    private ExcelUploadUtils excelUploadUtils;

    @Autowired
    ModelMapper modelMapper;


    public EmployeeServiceImpl(EmployeeRepo employeeRepo, OfficeRepo officeRepo, UserRepo userRepo, EmployeeConverter employeeConverter, EmployeeMapper employeeMapper, SectionMapper sectionMapper, SectionDesignationRepo sectionDesignationRepo, PositionService positionService, ServiceGroupService serviceGroupService, TransferMapper transferMapper, AttendanceServiceData attendanceServiceData,
                               RoleGroupRepo roleGroupRepo, DateConverter dateConverter) {
        this.employeeRepo = employeeRepo;
        this.officeRepo = officeRepo;
        this.userRepo = userRepo;
        this.employeeConverter = employeeConverter;
        this.employeeMapper = employeeMapper;
        this.sectionMapper = sectionMapper;
        this.sectionDesignationRepo = sectionDesignationRepo;
        this.positionService = positionService;
        this.serviceGroupService = serviceGroupService;
        this.transferMapper = transferMapper;
        this.attendanceServiceData = attendanceServiceData;
        this.roleGroupRepo = roleGroupRepo;
        this.dateConverter = dateConverter;
    }

    @Override
    @SneakyThrows
    public KararEmployeeChildPojo employeeAllDetail(String pisCode) {
        KararEmployeeChildPojo kararEmployeeChildPojo = new KararEmployeeChildPojo();
        KararEmployeePojo kararEmployeePojo = employeeConverter.toKararDto(employeeRepo.findByPisCode(pisCode));
        BeanUtils.copyProperties(kararEmployeeChildPojo, kararEmployeePojo);
        kararEmployeeChildPojo.setDistrict(officeMapper.getOfficeDistrict(kararEmployeePojo.getOfficeCode()));
        return kararEmployeeChildPojo;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }

    @Override
    public List<EmployeeMinimalPojo> getAllEmployeesWithHigherOrder() {
//        Long sectionId =  employeeMapper.getSectionId(tokenProcessorService.getPisCode());

        if (tokenProcessorService.getIsOfficeHead()) {
            // if parent office head is not initialize send office head of itself
            String officeCode = tokenProcessorService.getOfficeCode();
            String parentOfficeCode = officeMapper.getParentOfficeCode(officeCode);
            if (parentOfficeCode == null)
                return Collections.EMPTY_LIST;
            String pisCode = employeeMapper.getOfficeHeadCode(parentOfficeCode);
            if (pisCode == null)
                pisCode = employeeMapper.getOfficeHeadCode(officeCode);
            if (pisCode == null)
                return Collections.EMPTY_LIST;
            return employeeMapper.getEmployeeByPisCode(pisCode);
        } else {
            PositionPojo position = positionService.positionDetailByPis(tokenProcessorService.getPisCode());
            List<String> positionIds = positionService.getAllNodePositionCodeWithSelf(position.getCode(), position.getOrderNo());

            return employeeMapper.officeHierarchyEmployeeList(positionIds, tokenProcessorService.getOfficeCode());
        }
    }

    @Override
    public List<EmployeeMinimalPojo> getAllEmployeesWithHigherOrderWithoutRole() {
        List<String> officeIds = officeMapper.getAllParentOfficeIds(tokenProcessorService.getOfficeCode());
        List<EmployeeMinimalPojo> employees = employeeMapper.getOfficeHeadsFromOfficeCodes(officeIds);

        // getting current office approval employees
        PositionPojo position = positionService.positionDetailByPis(tokenProcessorService.getPisCode());
        List<String> positionIds = positionService.getAllNodePositionCodeWithSelf(position.getCode(), position.getOrderNo());
        employees.addAll(employeeMapper.officeHierarchyEmployeeListWithoutRole(positionIds, tokenProcessorService.getOfficeCode()));
        if (!tokenProcessorService.getIsOfficeHead()) {
            employees = employees.
                    stream().filter(e -> !e.getPisCode().equals(tokenProcessorService.getPisCode())).collect(Collectors.toList());
        }
        return employees;
    }

    @Override
    public List<EmployeeMinimalPojo> getAllEmployeesWithHigherOrderForEAttendance() {
        // getting parent office heads
        Office office = officeRepo.findById(tokenProcessorService.getOfficeCode()).orElseThrow( ()-> new CustomException("Office not found"));
//        List<String> officeIds = officeMapper.getAllParentOfficeIds(tokenProcessorService.getOfficeCode());
        List<String> officeIds = Arrays.asList(office.getParent().getCode());
        List<EmployeeMinimalPojo> employees = employeeMapper.getOfficeHeadsFromOfficeCodes(officeIds);

        // getting current office approval employees
//        PositionPojo position = positionService.positionDetailByPis(tokenProcessorService.getPisCode());
//        List<String> positionIds = positionService.getAllNodePositionCodeWithSelf(position.getCode(), position.getOrderNo());
//        employees.addAll(employeeMapper.officeHierarchyEmployeeList(positionIds, tokenProcessorService.getOfficeCode()));
        employees.addAll(this.getAllEmployeesWithPrivilllege());

        if (!tokenProcessorService.getIsOfficeHead()) {
            employees = employees.
                    stream().filter(e -> !e.getPisCode().equals(tokenProcessorService.getPisCode())).collect(Collectors.toList());
        }
        return employees;
//        if(tokenProcessorService.getIsOfficeHead()){
//            // get parent office head
//            // if parent office head is not initialize send office head of itself
//            String officeCode = tokenProcessorService.getOfficeCode();
//            String parentOfficeCode = officeMapper.getParentOfficeCode(officeCode);
//            if(parentOfficeCode == null)
//                return Collections.EMPTY_LIST;
//            String pisCode = employeeMapper.getOfficeHeadCode(parentOfficeCode);
//            if(pisCode == null)
//                pisCode = employeeMapper.getOfficeHeadCode(officeCode);
//            if(pisCode == null)
//                return Collections.EMPTY_LIST;
//            List<EmployeeMinimalPojo> e = employeeMapper.getEmployeeByPisCode(pisCode);
//            return e;
//        }else {
//
//        }
    }

    @Override
    public List<EmployeePojo> getAllEmployeesWithLowerOrder() {

        return employeeConverter.toDto(employeeRepo.getLowerHierarchyEmployee(tokenProcessorService.getPisCode()));
    }

    @Override
    public List<EmployeePojo> getAllEmployeesUserWithLowerOrder() {

        return employeeConverter.toDto(employeeRepo.getLowerHierarchyEmployeeUsers(tokenProcessorService.getPisCode()));
    }


    @Override
    public List<EmployeeMinimalPojo> sectionLowerEmployees(Long sectionId) {
        PositionPojo position = positionService.positionDetailByPis(tokenProcessorService.getPisCode());
        List<String> positionIds = positionService.getAllLowerNodePositionCodeWithSelf(position.getCode(), position.getOrderNo());

        return employeeMapper.sectionHierarchyEmployeeList(positionIds, sectionId);

    }

    @Override
    public List<EmployeeMinimalPojo> getAllEmployeesWithLowerOrderMinimal() {
        PositionPojo position = positionService.positionDetailByPis(tokenProcessorService.getPisCode());
        if (position == null) {//        List<OfficePojo> office = officeService.getAllGIOMSActiveOffice();

            throw new RuntimeException("Employee doesn't have any position");
        }
        List<String> positionIds = positionService.getAllLowerNodePositionCodeWithSelf(position.getCode(), position.getOrderNo());

        return employeeMapper.officeHierarchyEmployeeList(positionIds, tokenProcessorService.getOfficeCode());
    }

    @Override
    public EmployeePojo employeeDetail(String pisCode) {
        EmployeePojo employeePojo = employeeMapper.getByCode(pisCode);
        if (employeePojo != null && employeePojo.getService() != null && employeePojo.getService().getCode() != null) {
            if(employeePojo.getOffice().getCode() != null){
//                EmployeePojo employeePojo1 = findEmployeeShift(employeePojo);
                findEmployeeShift(employeePojo);
            }
            return findServiceGroupAndSubGroup(employeePojo, employeePojo.getService().getCode());
        }
        else
            return employeePojo;
    }

    @Override
    public EmployeePojo employeeDetail() {
        return employeeDetail(tokenProcessorService.getPisCode());
    }

    private EmployeePojo findServiceGroupAndSubGroup(EmployeePojo employeePojo, String code) {
        Map<String, Object> map = serviceGroupService.findServiceIdWithHierarchy(code);
        employeePojo.setService(null);
        employeePojo.setServiceGroup(null);
        employeePojo.setSubGroup(null);
        employeePojo.setService((ServicePojo) map.get(StringConstants.SERVICE));
        employeePojo.setGroup((ServicePojo) map.get(StringConstants.GROUP));
        employeePojo.setSubGroup((ServicePojo) map.get(StringConstants.SUBGROUP));

        return employeePojo;
    }

    private EmployeePojo findEmployeeShift(EmployeePojo employeePojo){
        EmployeePojo employeePojo1;
        try {
//            employeePojo1 = employeeMapper.getEmployeeShift(LocalDate.now(), employeePojo.getOffice().getCode(),employeePojo.getPisCode(), LocalDate.now().getDayOfWeek().toString());

            Long shiftId = employeeMapper.getApplicableDefaultShift(employeePojo.getOffice().getCode(), LocalDate.now());
            employeePojo1 = employeeMapper.getEmployeeShift(shiftId, LocalDate.now().getDayOfWeek().toString());

            employeePojo.setShiftNameEn(employeePojo1.getShiftNameEn());
            employeePojo.setShiftNameNp(employeePojo1.getShiftNameNp());
            // removing trailing zero from the shiftcheck out time
            employeePojo.setShiftCheckInTime(employeePojo1.getShiftCheckInTime().substring(0, employeePojo1.getShiftCheckInTime().length()-3));
            employeePojo.setShiftCheckOutTime(employeePojo1.getShiftCheckOutTime().substring(0, employeePojo1.getShiftCheckOutTime().length()-3));
        } catch (Exception ex){
            log.error("Error getting the shift details for employee details ");
        }

        return employeePojo;
    }
    @Override
    public EmployeeMinimalPojo employeeDetailMinimal(String pisCode) {
        System.out.println("pisCode: "+pisCode);
        EmployeeMinimalPojo employeeMinimalPojo = employeeMapper.getByCodeMinimal(pisCode);
        System.out.println("employee: "+employeeMinimalPojo);
        return employeeMinimalPojo;
    }

    @Override
    public List<EmployeePojo> employeeListOfOffice() {
        return employeeMapper.getEmployeeListByOfficeCode(tokenProcessorService.getOfficeCode());
    }

    @Override
    public List<EmployeePojo> distinctEmployeeListOfOffice() {
        return employeeMapper.getDistinctEmployeeListByOfficeCode(tokenProcessorService.getOfficeCode());
    }

    @Override
    public List<EmployeeSectionPojo> getOfficeSectionEmployeeList() {
        ArrayList<EmployeeSectionPojo> sectionSubsections = this.sectionMapper.getOfficeSectionEmployeeList(tokenProcessorService.getOfficeCode());
        for (EmployeeSectionPojo sectionSubsection : sectionSubsections) {
            this.getSubsectionsWithEmployee(sectionSubsection);
        }
        return sectionSubsections;
    }

    private void getSubsectionsWithEmployee(EmployeeSectionPojo sectionSubsection) {
        ArrayList<EmployeeSectionPojo> childSectionSubsections =
                sectionMapper.getOfficeSubSectionEmployeeList(sectionSubsection.getOfficeCode(),
                        sectionSubsection.getId());
        for (EmployeeSectionPojo childSectionSubsection : childSectionSubsections) {
            getSubsectionsWithEmployee(childSectionSubsection);
        }
        sectionSubsection.setSubsection(childSectionSubsections);
    }

    @Override
    public List<EmployeeSectionPojo> getSectionEmployeeList(Long id) {
        return employeeMapper.getSectionEmployeeList(id, tokenProcessorService.getOfficeCode());
    }

    @Override
    public List<EmployeePojo> getEmployeeListBySectionId(Long id) {
        return employeeMapper.getEmployeesBySectionId(id);
    }

    @Override
    public List<EmployeeMinimalPojo> getHigherSectionEmployeeList(Long sectionId) {
        PositionPojo position = positionService.positionDetailByPis(tokenProcessorService.getPisCode());
        List<String> positionIds = positionService.getAllNodePositionCodeWithSelf(position.getCode(), position.getOrderNo());

        return employeeMapper.sectionHierarchyEmployeeList(positionIds, sectionId);
    }

    @Override
    @Transactional
    public Employee saveEmployee(KararEmployeePojo employeePojo) {
        Employee employee = employeeConverter.toKararEmployeeEntity(employeePojo);
        if (!employeePojo.getEmployeeServiceStatusCode().equals(EmployeeServiceStatusConstant.CONTRACT_EMPLOYEE_SERVICE_CODE)) {
            employee.setEmployeeJoiningDates(null);
        } else {
            try {
                if (!ObjectUtils.isEmpty(employeePojo.getEmployeeJoiningDates()) && !employeePojo.getEmployeeJoiningDates().isEmpty()) {
                    List<EmployeeJoiningDatePojo> el = employeePojo.getEmployeeJoiningDates().stream().filter(EmployeeJoiningDatePojo::getIsActive).collect(Collectors.toList());
                    if (el.size() != 1) {
                        throw new ServiceValidationException("Active Contract Period is required");
                    }
                    employee.setIsActive(true);
                    EmployeeJoiningDatePojo ep = el.get(0);
                    employee.setCurOfficeJoinDtEn(ep.getJoinDateEn());
                    employee.setCurOfficeJoinDtNp(ep.getJoinDateNp());
                }
            } catch (Exception ex) {
                throw new RuntimeException("Single Active Contract Period is required");
            }
        }
//        if(!employee.getOffice().getCode().equals(employeePojo.getOfficeCode())){
//            this.updateEmployeeOffice(employee.getPisCode(), employeePojo.getOfficeCode());
//        }
        employee.setEmployeeCode(null);
        Employee savedEmployee = employeeRepo.save(employee);
        savedEmployee.setEmployeeCode(savedEmployee.getPisCode());
        return employeeRepo.save(savedEmployee);
    }

    private void validateJoiningDate(EmployeeJoiningDatePojo employeeJoiningDate, String pisCode, Long id) {
        LocalDate maxEndDate = employeeMapper.maxEndDate(pisCode, id);
        LocalDate maxDate = employeeMapper.maxEndDate(pisCode, id);
        if (maxEndDate != null && (employeeJoiningDate.getJoinDateEn().compareTo(maxEndDate) <= 0 ||
                employeeJoiningDate.getEndDateEn().compareTo(maxEndDate) <= 0)) {
            throw new ServiceValidationException("Joining Date Must be Greater than Previous End Date");
        }
        if (maxDate != null && (employeeJoiningDate.getJoinDateEn().compareTo(maxDate) <= 0 ||
                employeeJoiningDate.getEndDateEn().compareTo(maxDate) <= 0)) {
            throw new ServiceValidationException("Joining Date Must be Greater than Previous End Date");
        }
//        if(employeeMapper.hasEmployeeJoinDate(pisCode, employeeJoiningDate.getJoinDateEn(), id) != 0) {
//            throw new ServiceValidationException("Invalid Employee Joining Date");
//        }

    }

    private void resetLeave() {

    }

    @Override
    @CacheEvict(cacheNames = {EmpCacheConst.CACHE_VALUE_MINIMAL ,
            EmpCacheConst.CACHE_VALUE_DETAIL,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_MINIMAL_EATT,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_DETAIL_EATT},
            key = "#pisCode", condition = "#pisCode !=null")
    public void updateUserOffice(String pisCode, String targetOfficeCode, String oldOfficeCode) {
        sectionDesignationRepo.updateSectionByOfficeAndEmployee(pisCode, oldOfficeCode);
        User user = userRepo.findByPisEmployeeCode(pisCode);
        Office o = officeRepo.findById(targetOfficeCode).orElseThrow(() -> new NullPointerException("no office found for transfer"));
        if (!ObjectUtils.isEmpty(user)) {
            user.setOfficeCode(o.getCode());
            userRepo.save(user);
        }

    }

    @CacheEvict(cacheNames = {EmpCacheConst.CACHE_VALUE_MINIMAL ,
            EmpCacheConst.CACHE_VALUE_DETAIL,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_MINIMAL_EATT,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_DETAIL_EATT},
            key = EmpCacheConst.CACHE_EVICT_KEY, condition = "#result.getPisCode() !=null")
    @Override
    public Employee updateEmployeeOffice(OrgTransferRequest orgTransferRequest) {
        Employee employee = employeeRepo.getOne(orgTransferRequest.getEmployeePisCode());
        Office o = officeRepo.findById(orgTransferRequest.getTargetOffice().getCode()).orElseThrow(() -> new CustomException(customMessageSource.get("file.not.found")));
        employee.setOffice(o);
        employee.setCurOfficeJoinDtEn(orgTransferRequest.getExpectedJoinDateEn());
        employee.setCurOfficeJoinDtNp(orgTransferRequest.getExpectedJoinDateNp());
       return employeeRepo.save(employee);
    }

    @Override
    @CacheEvict(cacheNames = {EmpCacheConst.CACHE_VALUE_MINIMAL ,
            EmpCacheConst.CACHE_VALUE_DETAIL,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_MINIMAL_EATT,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_DETAIL_EATT},
            key = "#employeePojo.getPisCode()", condition = "#employeePojo.getPisCode() !=null")
    public Employee updateEmployee(EmployeePojo employeePojo) {
        Employee employee = employeeRepo.getOne(employeePojo.getPisCode());
        if (ObjectUtils.isEmpty(employee)) {
            throw new RuntimeException("employee not found");
        }
        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(employee, employeePojo);
        } catch (Exception e) {
            throw new RuntimeException("id doesnot exists");
        }
        if (!employeePojo.getOffice().getCode().equals(employee.getOffice().getCode())) {
            System.out.println("inside");
            this.updateUserOffice(employee.getPisCode(), employee.getOffice().getCode(), employeePojo.getOfficeCode());
        }

        return employeeRepo.save(employee);
    }

    // todo
    //  Refactor
    //  this method has been very complicated due to the extreme use of mapper .
    //  need to replace this method by dividing in sub method
    @Transactional
    @Override
    @CacheEvict(cacheNames = {EmpCacheConst.CACHE_VALUE_MINIMAL,
            EmpCacheConst.CACHE_VALUE_DETAIL,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_MINIMAL_EATT,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_DETAIL_EATT},
            key = "#employeePojo.getPisCode()")
    public Employee updateKararEmployee(KararEmployeePojo employeePojo){
        Employee employee = employeeRepo.findById(employeePojo.getPisCode()).orElseThrow(() -> new RuntimeException("employee not found"));
        String oldOffice;
        if (employee.getOffice() != null) {
            oldOffice = employee.getOffice().getCode();
        } else {
            throw new ServiceValidationException("Employee Office Cannot be Empty");
        }
        if(employeePojo.getOfficeCode() ==null){
            throw new CustomException("Office Code must not be null");
        }
        if (tokenProcessorService.isOfficeAdmin() && !employeePojo.getOfficeCode().equals(oldOffice) && Boolean.TRUE.equals(employee.getOffice().getIsGiomsActive())) {
            throw new ServiceValidationException("You do not have privilege to pull employee from active office");
        }

        // copying the updated properties to existing employee
        Employee employee1 = employeeConverter.toKararEmployeeEntity(employeePojo);
//        log.info(employeePojo.getPositionCode());
//        log.info(employee1.getPosition().getCode());
        org.springframework.beans.BeanUtils.copyProperties(employee1,employee, CopyObjetUtils.getNullPropertyNames(employee1));
//        log.info(employee.getPosition().getCode());


        try {
            if(employeePojo.getMiddleNameEn() != null || employeePojo.getMiddleNameNp() !=null){
                employee.setMiddleNameEn(employeePojo.getMiddleNameEn().trim());
                employee.setMiddleNameNp(employeePojo.getMiddleNameNp().trim());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(customMessageSource.get("error.middle.name.invalid"));
        }
        if (!employee.getEmployeeServiceStatus().getCode().equals(EmployeeServiceStatusConstant.CONTRACT_EMPLOYEE_SERVICE_CODE)) {
            employee.setEmployeeJoiningDates(null);
        } else {
            try {
                // these function can be replaced with same method in future
                if (!ObjectUtils.isEmpty(employeePojo.getEmployeeJoiningDates()) && !employeePojo.getEmployeeJoiningDates().isEmpty()) {
                    List<EmployeeJoiningDatePojo> el = employeePojo.getEmployeeJoiningDates().stream().filter(EmployeeJoiningDatePojo::getIsActive).collect(Collectors.toList());
                    List<EmployeeJoiningDatePojo> newDates = employeePojo.getEmployeeJoiningDates().stream().filter(e -> e.getId() == null).collect(Collectors.toList());

                    if (employee.getEmployeeJoiningDates().size() > 1) {
                        if (!newDates.isEmpty()) {
                            newDates.forEach(ep -> validateJoiningDate(ep, employee.getPisCode(), ep.getId()));
                        }

                        if (el.size() != 1) {
                            throw new ServiceValidationException("Active Contract Period is required");
                        }

                    }

                    EmployeeJoiningDatePojo ep = el.get(0);
                    if (attendanceServiceData.resetLeave(employee.getPisCode(), ep.getJoinDateEn(), ep.getEndDateEn()) != null) {
                        log.info("successfully updated remaining leave");
                    }
                    if (LocalDate.now().compareTo(ep.getJoinDateEn()) < 0 && Boolean.TRUE.equals(ep.getIsActive())) {
                        throw new ServiceValidationException("Future Joining Date Cannot Be Active");
                    }
                    employee.setCurOfficeJoinDtEn(ep.getJoinDateEn());
                    employee.setCurOfficeJoinDtNp(ep.getJoinDateNp());
                }
            } catch (ServiceValidationException se) {
                log.info(se.getMessage());
                throw se;
            } catch (Exception ex) {
                throw new RuntimeException("Single Active Contract Period is required");
            }
        }
//       todo if(!employee.getOffice().getCode().equals(employeePojo.getOfficeCode())){
        System.out.println(employeePojo.getOfficeCode() + " " + oldOffice + " " + employee.getOffice().getCode());
        if (!employeePojo.getOfficeCode().equals(oldOffice)) {
            this.updateUserOffice(employee.getPisCode(), employee.getOffice().getCode(), oldOffice);
        }

//        employee.setEmployeeCode(employee.getPisCode());

        return employeeRepo.save(employee);
    }



    // depreciated
    @Override
    public EmployeeMinimalPojo getOfficeHeadEmployee(String officeCode) {
        return employeeMapper.getOfficeHeadEmployee(officeCode);
    }

    @Override
    public List<EmployeePojo> employeeListOfByOffice(String officeCode) {
        return employeeMapper.getEmployeeListByOfficeCodeWihDesignation(officeCode);
    }

    @Override
    public List<EmployeePojo> employeeAllDetailListOfByOffice(String officeCode) {
        Map<String,String> designationType = new HashMap<>();
        return employeeMapper.getEmployeeAllListByOfficeCodeWihDesignation(officeCode,designationType);
    }

    @Override
    public void saveEmployeeOnSection(List<String> employeeIds, Long sectionId) {
//        List<Employee> employees = employeeIds.stream().filter(s -> !ObjectUtils.isEmpty(employeeRepo.getOne(s)))
//                .map(s -> new Employee(s , sectionId)).collect(Collectors.toList());
//        if(employees.size() == employeeIds.size()) {
//            employeeRepo.saveAll(employees);
//        } else {
//            throw new RuntimeException("No Employee Found of Provided pis code");
//        }
    }

    @Override
    public List<EmployeePojo> searchEmployees(SearchPojo search) {
        Map<String, Object> searchParam = objectMapper.convertValue(search, Map.class);
        searchParam.put("isActive",true);
        return employeeMapper.getEmployeeByFilterParam(searchParam);
    }

    @Override
    public Page<EmployeePojo> employeeContact(GetRowsRequest paginatedRequest) {
        Page<EmployeePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        page = employeeMapper.searchCurrentOfficeContact(page, paginatedRequest.getSearchField(), tokenProcessorService.getOfficeCode(), tokenProcessorService.getUserId());
        return page;
    }

    @Override
    public Page<EmployeePojo> employeesPaginated(GetRowsRequest paginatedRequest) {
        Page<EmployeePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        if (!tokenProcessorService.isAdmin()) {
            paginatedRequest.getSearchField().put("officeCode", tokenProcessorService.getOfficeCode());
        }
        page = employeeMapper.searchEmployeesPaginated(page, paginatedRequest.getSearchField(), tokenProcessorService.getUserId(), tokenProcessorService.getOfficeCode());
        return page;
    }

    @Override
    public Page<EmployeePojo> employeeContactAllOffice(GetRowsRequest paginatedRequest) {
        Page<EmployeePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        page = employeeMapper.searchAllOfficeContact(page, paginatedRequest.getSearchField(), tokenProcessorService.getUserId());
        return page;
    }

    @Override
    @CacheEvict(cacheNames = {EmpCacheConst.CACHE_VALUE_MINIMAL ,
            EmpCacheConst.CACHE_VALUE_DETAIL,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_MINIMAL_EATT,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_DETAIL_EATT},
            key = "#employeePojo.getPisCode()", condition = "#employeePojo.getPisCode() !=null")
    public void updateEmployeeOrders(EmployeePojo employeePojo) {
        EmployeePojo employee = employeeMapper.getByCode(employeePojo.getPisCode());
        List<EmployeePojo> employeeMatchingOrders = employeeMapper.getEmployeeMatchingOrders(employee.getPositionCode(), employee.getCurrentPositionAppDateBs());
        employeeMatchingOrders.forEach(employeePojo1 -> {

        });
    }

    @CacheEvict(cacheNames = {EmpCacheConst.CACHE_VALUE_MINIMAL ,
            EmpCacheConst.CACHE_VALUE_DETAIL,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_DETAIL_EATT,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_MINIMAL_EATT}, key = "#employeePojo.pisCode", condition = "#employeePojo.pisCode !=null")
    @Override
    @Transactional
    public void activateUnAssignedEmployee(EmployeePojo employeePojo) {

        Employee employee = employeeRepo.findById(employeePojo.getPisCode()).orElseThrow(() -> new NullPointerException("Employee not found"));
        if (!employee.getEmployeeServiceStatus().getCode().equals(EmployeeServiceStatusConstant.UNASSIGNED_EMPLOYEE_SERVICE_CODE)) {
            throw new ServiceValidationException("Employee must be Unassigned Employee");
        } else {
            User user = userRepo.findByPisEmployeeCode(employee.getPisCode());
            if (user != null && employeePojo.getEmployeeCode() != null) {
                user.setUsername(employeePojo.getEmployeeCode().toLowerCase());
                userRepo.save(user);
            }
            EmployeeServiceStatus serviceStatus = new EmployeeServiceStatus();
            serviceStatus.setCode(EmployeeServiceStatusConstant.PERMANENT_EMPLOYEE_SERVICE_CODE);
            employee.setEmployeeServiceStatus(serviceStatus);

            employee.setEmployeeCode(employeePojo.getEmployeeCode());
            if(employeeRepo.getEmployeeCountByPisCode(employeePojo.getEmployeeCode()) > 0){
                throw new CustomException(customMessageSource.get("error.duplicate.data"," pis code "));
            }
            employeeRepo.save(employee);

        }

    }

    @CacheEvict(cacheNames = {EmpCacheConst.CACHE_VALUE_MINIMAL ,
            EmpCacheConst.CACHE_VALUE_DETAIL,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_DETAIL_EATT,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_MINIMAL_EATT}, key = "#pisCode")
    @Override
    @Transactional
    public void updatePisCode(EmployeePojo employeePojo, String pisCode) {

        if(!tokenProcessorService.isOfficeAdmin()) {
            throw new CustomException(customMessageSource.get("invalid.create.request"));
        }
            Employee employee = employeeRepo.findById(pisCode).orElseThrow(() -> new NullPointerException("Employee not found"));

            User user = userRepo.findByPisEmployeeCode(employee.getPisCode());
            if (user != null && employeePojo.getEmployeeCode() != null) {
                user.setUsername(employeePojo.getEmployeeCode().toLowerCase());
                userRepo.save(user);
            }
        if(employeeRepo.getEmployeeCountByPisCode(employeePojo.getEmployeeCode()) > 0){
            throw new CustomException(customMessageSource.get("error.duplicate.data"," pis code "));
        }
            employee.setEmployeeCode(employeePojo.getEmployeeCode());
            employeeRepo.save(employee);

    }

    @Override
    public EmployeeMinimalPojo getCurrentUser() {
        return employeeMapper.getByCodeMinimal(tokenProcessorService.isDelegated() ? tokenProcessorService.getPreferredUsername() : tokenProcessorService.getPisCode());
    }

    @CacheEvict(cacheNames = {EmpCacheConst.CACHE_VALUE_MINIMAL ,
            EmpCacheConst.CACHE_VALUE_DETAIL,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_MINIMAL_EATT,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_DETAIL_EATT}, key = "#employeePromotion.pisCode")
    @Override
    @Transactional
    public void promoteEmployeeJobDetail(EmployeePromotionPojo employeePromotion) {
        Employee employee = employeeRepo.getOne(employeePromotion.getPisCode());
        employeePromotion.setIsPromotion(Boolean.TRUE);
        // todo change into auto logging by creating pointcut
        employeePromotionLogAspect.logAction(employee, employeePromotion);

        employee.setService(employeePromotion.getService().getEntity());
        employee.setPosition(employeePromotion.getPosition().getEntity());
        employee.setDesignation(employeePromotion.getFunctionalDesignation().getEntity());
        employee.setCurrentPositionAppDateAd(LocalDate.now());
        employee.setCurrentPositionAppDateBs(new DateConverter().getCurrentNepaliDate());

        sectionDesignationRepo.updateSectionByOfficeAndEmployee(employeePromotion.getPisCode(), tokenProcessorService.getOfficeCode());
        employeeRepo.save(employee);
    }

    @Override
    public List<EmployeeErrorMessagePojo> uploadExcel(MultipartFile file) {
        List<EmployeeErrorMessagePojo> employeeErrorMessage = new ArrayList<>();
        try {
            List<Employee> employees = excelUploadUtils.excelToEmployeeEntity(file);
            employees.forEach(e -> {
                try {
                    saveExcelData(e);
                } catch (Exception ex) {
                    String message = "";
                    message = getMessage(e);
                    EmployeeErrorMessagePojo employeePojo = new EmployeeErrorMessagePojo();
                    employeePojo.setNameEn(e.getFirstNameEn() + " " + e.getLastNameEn());
                    employeePojo.setNameNp(e.getFirstNameNp() + " " + e.getLastNameNp());
                    employeePojo.setPisCode(e.getPisCode());

                    employeePojo.setErrorMessage(message);
                    employeeErrorMessage.add(employeePojo);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServiceValidationException(ex.getMessage());
        }
        return employeeErrorMessage;
    }

    @Override
    public List<EmployeeMinimalPojo> getSpecialEmployeeList() {
        return employeeMapper.getEmployeeByDesignationType(DesignationType.SPECIAL_DESIGNATION.toString(), tokenProcessorService.getOfficeCode());
    }

    /**
     * pagination list admin level created by super admin
     *
     * @param paginatedRequest
     * @return
     */
    @Override
    public Page<EmployeePojo> employeesAdminPaginated(GetRowsRequest paginatedRequest) {
        Page<EmployeePojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        if (!tokenProcessorService.isAdmin() && !tokenProcessorService.isOrganisationAdmin()) {
            throw new RuntimeException(customMessageSource.get("user.unauthorized", null));
        }
        if (!tokenProcessorService.isAdmin() && !tokenProcessorService.isOrganisationAdmin()) {
            paginatedRequest.getSearchField().put("officeCode", tokenProcessorService.getOfficeCode());
        }

        page = employeeMapper.searchEmployeesAdminPaginated(page, paginatedRequest.getSearchField(),
                Arrays.asList(StringConstants.OFFICE_ADMINISTRATOR_ROLE.toString(), StringConstants.ORGANISATION_ADMIN.toString()));
//        return page;
        page.getRecords().parallelStream().forEachOrdered(x->{
                    x.setCreateDateNp(dateConverter.convertAdToBs(String.valueOf(x.getAdminRoleCreatedDate().toLocalDateTime().toLocalDate())));
                    x.setUpdateDateNp(dateConverter.convertAdToBs(String.valueOf(x.getAdminRoleUpdatedDate().toLocalDateTime().toLocalDate())));
                });
//        page = employeeMapper.searchEmployeesPaginated(page, paginatedRequest.getSearchField(), tokenProcessorService.getUserId(), tokenProcessorService.getOfficeCode());
        return page;
    }

    @Override
    public List<EmployeePojo> employeesAdminPrint(String fromDate,String toDate) {
        LocalDate fromDates=dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(fromDate));
        LocalDate toDates=dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(toDate));
      List<EmployeePojo>  page = employeeMapper.employeesAdminPrint(
              tokenProcessorService.getOfficeCode(),
              fromDates,toDates,
              Arrays.asList(StringConstants.OFFICE_ADMINISTRATOR_ROLE.toString(), StringConstants.ORGANISATION_ADMIN.toString()));
        page.parallelStream().forEachOrdered(x->{
            x.setCreateDateNp(dateConverter.convertAdToBs(String.valueOf(x.getAdminRoleCreatedDate().toLocalDateTime().toLocalDate())));
            x.setUpdateDateNp(dateConverter.convertAdToBs(String.valueOf(x.getAdminRoleUpdatedDate().toLocalDateTime().toLocalDate())));
        });
        return page;
    }

    @Override
    public List<EmployeeMinimalPojo> getKararEmployeeList(int days) {
        return employeeMapper.getKararEmployeeList(tokenProcessorService.getOfficeCode(), days);
    }

    @Override
    public Map<String, Object> getKararEmployeeCount(int days) {
        String officeCode = tokenProcessorService.getOfficeCode();
        Map<String, Object> count = new HashMap<>();
        count.put("count", employeeRepo.getEmployeeValidCount(officeCode, days).get(0));
        return count;
    }

    @Validated
    private Employee saveExcelData(@Valid Employee e) {
        return employeeRepo.save(e);

    }

    @Override
    public Employee detail(String pisCode) {
        return employeeRepo.findById(pisCode).orElseThrow(() -> new ServiceValidationException("No Employee found"));
    }

    private String getMessage(Employee employee) {
        if (employee.getPisCode() == null) {
            return "Invalid PisCode";
        }
        if (employee.getFirstNameEn() == null || employee.getLastNameEn() == null ||
                employee.getFirstNameNp() == null || employee.getLastNameNp() == null) {
            return "Invalid Employee name";
        }
        return "Error while updating employee";
    }

    private void checkValidData(Employee employee) {
        if (employee.getPisCode() == null || employee.getFirstNameEn() == null || employee.getLastNameEn() == null ||
                employee.getFirstNameNp() == null || employee.getLastNameNp() == null) {
            throw new ServiceValidationException("Invalid Excel Data Format");
        }
    }

    @Override
    public List<TMSEmployeePojo> getEmployeeListDetailByOffice(String officeCode) {
        List<TMSEmployeePojo> employeePojos = employeeMapper.getAllEmployeeByOfficeCode(officeCode);
        return employeePojos;
    }

    @Override
    public List<TMSEmployeePojo> getAllEmployeeList() {
        return employeeMapper.getAllEmployee();
    }

    @Override
    public List<TMSEmployeePojo> getAllEmployeeById(List<Long> userIds) {
        return employeeMapper.getAllEmployeeByIds(userIds);
    }

    @Override
    public TMSEmployeePojo getClientIdByPisCode(TMSClientDetailRequestPojo tmsClientDetailRequestPojo) {
    // todo: convert token into piscode
        String pisCode = tmsClientDetailRequestPojo.getToken();
        return employeeMapper.getTMSEmployeeByPisCode(pisCode);
    }

    @Override
    @CacheEvict(cacheNames = {EmpCacheConst.CACHE_VALUE_MINIMAL ,
            EmpCacheConst.CACHE_VALUE_DETAIL,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_MINIMAL_EATT,
            EmpCacheConst.CACHE_VALUE_EMPLOYEE_DETAIL_EATT},
            key = "#profilePic.getPisCode()", condition = "#profilePic.getPisCode() !=null")
    public Boolean saveProfilePic(EmployeeMinimalPojo profilePic){
        Long MAX_SIZE = 1722595L;
        Employee employee = employeeRepo.findById(profilePic.getPisCode()).orElseThrow(() -> new RuntimeException("employee not found"));
        if(profilePic.getFile().getSize() > MAX_SIZE){
            throw new CustomException(customMessageSource.get("image.size.error"));
        }
//        if(profilePic.getProfilePic().isEmpty()){
//            throw new RuntimeException("Fields Empty");
//        }
        try{
            String base64String = convertSize(profilePic.getFile());
            employee.setProfilePic(base64String);
        } catch (Exception ex){
            log.error("error uploading image ", ex);
            throw new CustomException("File formate not matched ");

        }

        employee = employeeRepo.save(employee);
        return true;
    }

    @Override
    @Transactional
    public String saveEmployeeForWizard(KararEmployeePojo kararEmployeePojo) {
        // save the employee and create use with role general user from wizard
        kararEmployeePojo.setOfficeCode(tokenProcessorService.getOfficeCode());
        Employee employee = saveEmployee(kararEmployeePojo);
        RoleGroup generalUserRole = roleGroupRepo.findByKey(StringConstants.GENERAL_USER_ROLE).get();
        List<RoleGroup>  roleList = new ArrayList<>();
        roleList.add(generalUserRole);
        if(!kararEmployeePojo.getPassword().equals(kararEmployeePojo.getReenterPassword())) throw  new CustomException(customMessageSource.get("error.password"));


        User user = new User().builder().username(employee.getPisCode().trim().toLowerCase()).password(DefaultPasswordEncoderFactories.createDelegatingPasswordEncoder().encode(kararEmployeePojo.getPassword())).roles(roleList).isPasswordChanged(false)
                .pisEmployeeCode(employee.getPisCode()).officeCode(tokenProcessorService.getOfficeCode()).build();
        user.setActive(true);

        user = userRepo.save(user);
        if(user.getId() ==null) throw  new CustomException(customMessageSource.get("error.user.save"));

        return employee.getPisCode();
    }
    private  String convertSize(MultipartFile file) throws IOException {
        BufferedImage img = ImageIO.read(file.getInputStream());
        int height = 256;
        int width = 256;
        Image resizedImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bufferedResizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        bufferedResizedImage.getGraphics().drawImage(resizedImage, 0, 0, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedResizedImage,"jpg", byteArrayOutputStream);

        byte[] base64Bytes = Base64.getEncoder().encode(byteArrayOutputStream.toByteArray());
        String base64String = "data:image/png;base64,";
        base64String = base64String.concat(new String(base64Bytes));

        return base64String;
    }

    @Override
    public EmployeePojo employeeDetailFromEmployeeCode(String employeeCode) {

        EmployeePojo employeePojo = employeeMapper.getByEmployeeCode(employeeCode);
        if (employeePojo != null && employeePojo.getService() != null && employeePojo.getService().getCode() != null) {
            return findServiceGroupAndSubGroup(employeePojo, employeePojo.getService().getCode());
        }
        else
            return employeePojo;
    }

    @Override
    public List<EmployeeMinimalPojo> getAllEmployeesWithPrivilllege() {
        String officeCode = tokenProcessorService.getOfficeCode();
        List<String>  moduleKeys = new ArrayList<>();
        moduleKeys.add(ModuleKeyEnum.KAAJAPPROVAL.toString());
        moduleKeys.add(ModuleKeyEnum.LEAVEAPPROVAL.toString());
        moduleKeys.add(ModuleKeyEnum.DAILYLOGAPPROVAL.toString());

        List<String> privileges = Arrays.asList(Privilege.APPROVE.toString(), Privilege.REVIEW.toString(), Privilege.REVERT.toString());

        List<EmployeeMinimalPojo> result = employeeMapper.getEmployeeBasedOnModulePrivilege(privileges, moduleKeys, officeCode);

        return result.stream().filter( distinctByKey(EmployeeMinimalPojo::getPisCode)).collect(Collectors.toList());
    }


    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
