package com.gerp.usermgmt.services.delegation.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.constants.cacheconstants.EmpCacheConst;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.pojo.NotificationPojo;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.gerp.usermgmt.enums.DesignationType;
import com.gerp.usermgmt.mapper.delegation.DelegationMapper;
import com.gerp.usermgmt.mapper.usermgmt.UserMapper;
import com.gerp.usermgmt.model.delegation.Delegation;
import com.gerp.usermgmt.pojo.delegation.TempDelegationPojo;
import com.gerp.usermgmt.pojo.delegation.TempDelegationResponsePojo;
import com.gerp.usermgmt.pojo.organization.employee.EmployeePojo;
import com.gerp.usermgmt.repo.delegation.DelegationRepository;
import com.gerp.usermgmt.services.delegation.DelegationService;
import com.gerp.usermgmt.services.organization.employee.EmployeeService;
import com.gerp.usermgmt.services.rabbitmq.RabbitMQService;
import com.gerp.usermgmt.token.TokenProcessorService;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DelegationServiceImpl implements DelegationService {

    private final DelegationRepository delegationRepository;
    private final CustomMessageSource customMessageSource;
    private final TokenProcessorService tokenProcessorService;
    private final DelegationMapper delegationMapper;
    private final EmployeeService employeeService;
    private final DateConverter dateConverter;
    private final UserMapper userMapper;

    @Autowired
    private RabbitMQService notificationService;
    public DelegationServiceImpl(DelegationRepository delegationRepository, CustomMessageSource customMessageSource, TokenProcessorService tokenProcessorService, DelegationMapper delegationMapper, EmployeeService employeeService, DateConverter dateConverter, UserMapper userMapper) {
        this.delegationRepository = delegationRepository;
        this.customMessageSource = customMessageSource;
        this.tokenProcessorService = tokenProcessorService;
        this.delegationMapper = delegationMapper;
        this.employeeService = employeeService;
        this.dateConverter = dateConverter;
        this.userMapper = userMapper;
    }

    @SneakyThrows
    @Override
    public int addDelegation(TempDelegationPojo tempDelegationPojo) {
        if (tempDelegationPojo.getFromPisCode().equalsIgnoreCase(tempDelegationPojo.getToPisCode())){
            throw new CustomException(customMessageSource.get("invalid.delegation.request"));
        }
        Delegation delegation = new Delegation();
        if (tempDelegationPojo.getExpireDate().isBefore(tempDelegationPojo.getEffectiveDate())){
            throw new CustomException(customMessageSource.get("invalid.date.after","लागु हुने मिति","म्याद सकिने मिति"));
        }
//        if (delegationMapper.existsActiveDelegation(tempDelegationPojo.getFromPisCode()) > 0 || delegationMapper.existsByExpireAndEffectiveDate(tempDelegationPojo.getExpireDate(),tempDelegationPojo.getEffectiveDate(),tempDelegationPojo.getFromPisCode()) > 0){
//            throw new CustomException(customMessageSource.get("invalid.request"));
//        }
        if (delegationMapper.existsByExpireAndEffectiveDate(tempDelegationPojo.getExpireDate(),tempDelegationPojo.getEffectiveDate(),tempDelegationPojo.getFromPisCode()) > 0){
            throw new CustomException(customMessageSource.get("delegation.present"));
        }
        EmployeePojo fromEmployeePojo = employeeService.employeeDetail(tempDelegationPojo.getFromPisCode());
        EmployeePojo toEmployeePojo = employeeService.employeeDetail(tempDelegationPojo.getToPisCode());
        BeanUtils.copyProperties(tempDelegationPojo,delegation);
        delegation.setFromDesignationId(fromEmployeePojo.getFunctionalDesignationCode());
        // special designation employee does not contain position
        if(fromEmployeePojo.getFunctionalDesignation().getDesignationType().equals(DesignationType.NORMAL_DESIGNATION.toString())){
            delegation.setFromPositionCode(fromEmployeePojo.getCoreDesignation().getCode());
        }
        // special designation employee does not contain position
        if(fromEmployeePojo.getFunctionalDesignation().getDesignationType().equals(DesignationType.NORMAL_DESIGNATION.toString())){
            delegation.setToPositionCode(toEmployeePojo.getCoreDesignation().getCode());
        }
        delegation.setToDesignationId(toEmployeePojo.getFunctionalDesignationCode());
        Integer officeHead = delegationMapper.isOfficeHead(tempDelegationPojo.getFromPisCode());
        delegation.setIsOfficeHead(officeHead != null && officeHead > 0);
        delegationRepository.saveAndFlush(delegation);


        notificationService.notificationProducer(NotificationPojo.builder()
                .moduleId((long) delegation.getId())
                .module("DELEGATION")
                .sender(tokenProcessorService.getPisCode())
                .receiver(tempDelegationPojo.getToPisCode())
                .subject("प्रतनिधिमिण्डल/पुन नयिुक्ति")
                .detail(customMessageSource.getNepali("delegation.message", getDateConvertedToNepaliDate(tempDelegationPojo.getEffectiveDate()),getDateConvertedToNepaliDate(tempDelegationPojo.getExpireDate()),fromEmployeePojo.getNameNp()))
                .pushNotification(true)
                .received(true)
                .build());
        return delegation.getId();
    }

    private String getDateConvertedToNepaliDate(LocalDateTime dateTime) {
        return dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(dateTime.toLocalDate().toString()));
    }

    @Override
    @CacheEvict(value = EmpCacheConst.CACHE_VALUE_DELEGATION_RESPONSE_POJO, key = "#tempDelegationPojo.getId()", condition = "#tempDelegationPojo.getId() != null")
    public int updateDelegation(TempDelegationPojo tempDelegationPojo) {
        if (tempDelegationPojo.getId() == 0){
            throw new CustomException(customMessageSource.get(CrudMessages.notExist,"Id"));
        }
        Delegation delegation = getDelegation(tempDelegationPojo.getId());
        if (!delegation.getCreatedBy().equals(tokenProcessorService.getUserId()) && !delegation.getIsReassignment()){
            throw new CustomException(customMessageSource.get(CrudMessages.notPermit,"update"));
        }
        if(tempDelegationPojo.getIsAbort().equals(true)){
            delegation.setExpireDate(LocalDateTime.now());
            return delegationRepository.save(delegation).getId();
        }

        if (tempDelegationPojo.getExpireDate().isBefore(tempDelegationPojo.getEffectiveDate())){
            throw new CustomException(customMessageSource.get("invalid.date.after","effective date","expire date"));
        }
        if (delegationMapper.existsByExpireAndEffectiveDateAndIsNotId(tempDelegationPojo.getExpireDate(),tempDelegationPojo.getEffectiveDate(),tempDelegationPojo.getFromPisCode(), delegation.getId()) > 0){
            throw new CustomException(customMessageSource.get("delegation.present"));
        }

        BeanUtils.copyProperties(tempDelegationPojo,delegation);
        delegationRepository.save(delegation);
        return delegation.getId();
    }

    private Delegation getDelegation(int id) {
        return delegationRepository.findById(id)
                    .orElseThrow(()->new CustomException(customMessageSource.get(CrudMessages.notExist,"Delegation")));
    }

    @Override
    public Page<TempDelegationResponsePojo> getTemporaryDelegation(String searchKey, int limit, int pageNo, Boolean isReassignment) {
        Page<TempDelegationResponsePojo> page = new Page<>(pageNo,limit);
        return delegationMapper.getTemporaryDelegation(page,searchKey,tokenProcessorService.getUserId(), LocalDateTime.now(),isReassignment, tokenProcessorService.getOfficeCode());
    }

    @Override
    public Page<TempDelegationResponsePojo> getTemporaryDelegationList(String searchKey, int limit, int pageNo, Boolean isDelegatedSelf, Boolean isReassignment) {
        Page<TempDelegationResponsePojo> page = new Page<>(pageNo,limit);
           if (tokenProcessorService.isDelegated()){
            return     delegationMapper.getUserDetails(page,tokenProcessorService.getPreferredUsername());
       }else {
             return   delegationMapper.getTemporaryDelegationForUser(page, searchKey,tokenProcessorService.getPisCode(),isDelegatedSelf,LocalDateTime.now(),isReassignment, tokenProcessorService.getOfficeCode());
           }

    }

    @Override
    public int deletTempDelegation(int id) {
        Delegation delegation = getDelegation(id);
        if (!delegation.getCreatedBy().equals(tokenProcessorService.getUserId()) && !delegation.getIsReassignment()){
            throw new CustomException(customMessageSource.get(CrudMessages.notPermit,"delete"));
        }
        if(delegation.getActive().equals(Boolean.FALSE) && delegationMapper.existsByExpireAndEffectiveDateAndIsNotId(delegation.getExpireDate(),delegation.getEffectiveDate(),delegation.getFromPisCode(), delegation.getId()) > 0){
            throw new CustomException(customMessageSource.get("delegation.present"));
        }

        delegationRepository.deleteById(id);
        return id;
    }

    @Override
    public TempDelegationResponsePojo getTemporaryDelegationById(int id) {
        return delegationMapper.getTemporaryDelegationById(id);
    }

    @Override
    public Integer activeDelegation(String pisCode) {
//        return delegationMapper.existsActiveDelegation(pisCode);
        return  delegationMapper.existsDelegationByTime(LocalDateTime.now() , pisCode);
    }

    @Override
    public List<TempDelegationResponsePojo> getAllDelegation(String pisCode, Boolean isReassignment) {

        List<TempDelegationResponsePojo>  tempDelegationResponsePojos = delegationMapper.getAllDelegation(pisCode, isReassignment);
        return tempDelegationResponsePojos;
    }
}
