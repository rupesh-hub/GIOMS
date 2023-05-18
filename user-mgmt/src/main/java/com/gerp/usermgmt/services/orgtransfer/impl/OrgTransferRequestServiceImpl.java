package com.gerp.usermgmt.services.orgtransfer.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.ResponseStatus;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.GlobalApiResponse;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.gerp.usermgmt.Proxy.AttendanceServiceData;
import com.gerp.usermgmt.component.OrgTransferNotificationProducer;
import com.gerp.usermgmt.config.generator.EmployeeServiceStatusConstant;
import com.gerp.usermgmt.converter.organiztion.orgtransfer.OrgTransferRequestConverter;
import com.gerp.usermgmt.enums.TransferAction;
import com.gerp.usermgmt.enums.TransferStatus;
import com.gerp.usermgmt.mapper.organization.orgtransfer.OrgTransferRequestMapper;
import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.model.orgtransfer.OrgTransferHistory;
import com.gerp.usermgmt.model.orgtransfer.OrgTransferRequest;
import com.gerp.usermgmt.pojo.device.PisCodeToDeviceMapperPojo;
import com.gerp.usermgmt.pojo.organization.orgtransfer.OrgTransferRequestPojo;
import com.gerp.usermgmt.repo.orgtransfer.OrgTransferRequestRepo;
import com.gerp.usermgmt.services.auth.UserService;
import com.gerp.usermgmt.services.delegation.DelegationService;
import com.gerp.usermgmt.services.organization.employee.EmployeeService;
import com.gerp.usermgmt.services.organization.office.SectionDesignationService;
import com.gerp.usermgmt.services.orgtransfer.OrgTransferHistoryService;
import com.gerp.usermgmt.services.orgtransfer.OrgTransferRequestService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;

@Service
public class OrgTransferRequestServiceImpl implements OrgTransferRequestService {
    private final OrgTransferRequestRepo orgTransferRequestRepo;
    private final UserService userService;
    private final DateConverter dateConverter;
    private final OrgTransferHistoryService orgTransferHistoryService;
    private final OrgTransferRequestConverter orgTransferRequestConverter;
    private final TokenProcessorService tokenProcessorService;
    private final OrgTransferRequestMapper orgTransferRequestMapper;
    private final EmployeeService employeeService;
    private final AttendanceServiceData attendanceServiceData;
    private final SectionDesignationService sectionDesignationService;
    private final DelegationService delegationService;
    private final OrgTransferNotificationProducer orgTransferNotificationProducer;
    private final CustomMessageSource customMessageSource;
    @Autowired private ModelMapper modelMapper;

    public OrgTransferRequestServiceImpl(OrgTransferRequestRepo orgTransferRequestRepo, UserService userService, DateConverter dateConverter, OrgTransferHistoryService orgTransferHistoryService, OrgTransferRequestConverter orgTransferRequestConverter, TokenProcessorService tokenProcessorService, OrgTransferRequestMapper orgTransferRequestMapper, EmployeeService employeeService, AttendanceServiceData attendanceServiceData, SectionDesignationService sectionDesignationService, DelegationService delegationService, OrgTransferNotificationProducer orgTransferNotificationProducer
    ,CustomMessageSource customMessageSource) {
        this.orgTransferRequestRepo = orgTransferRequestRepo;
        this.userService = userService;
        this.dateConverter = dateConverter;
        this.orgTransferHistoryService = orgTransferHistoryService;
        this.orgTransferRequestConverter = orgTransferRequestConverter;
        this.tokenProcessorService = tokenProcessorService;
        this.orgTransferRequestMapper = orgTransferRequestMapper;
        this.employeeService = employeeService;
        this.attendanceServiceData = attendanceServiceData;
        this.sectionDesignationService = sectionDesignationService;
        this.delegationService = delegationService;
        this.orgTransferNotificationProducer = orgTransferNotificationProducer;
        this.customMessageSource = customMessageSource;
    }

    @Transactional
    @Override
    public Long requestTransfer(OrgTransferRequestPojo orgTransferRequestPojo) {
        this.validateTransfer(orgTransferRequestPojo);

        OrgTransferRequest orgTransferRequest = orgTransferRequestConverter.toEntity(orgTransferRequestPojo);


        orgTransferRequest.setTransferStatus(TransferStatus.P);
        orgTransferRequest.setTransferRequestFrom(tokenProcessorService.getPisCode());
        orgTransferRequest.setRequestedOffice(new Office(tokenProcessorService.getOfficeCode()));

        orgTransferRequest.setRequestedDateEn(LocalDate.now());
        orgTransferRequest.setRequestedDateNp(dateConverter.convertAdToBs(LocalDate.now().toString()));

        orgTransferRequest = orgTransferRequestRepo.save(orgTransferRequest);

        addActionHistory(orgTransferRequest);
        orgTransferNotificationProducer.sendNotification(orgTransferRequest,TransferAction.RQ.toString());

        return orgTransferRequest.getId();
    }

    private void validApprove(OrgTransferRequest orgTransferRequest) {
        if(orgTransferRequest.getTransferStatus() == TransferStatus.A || orgTransferRequest.getTransferStatus() == TransferStatus.C
        || orgTransferRequest.getTransferStatus() == TransferStatus.R) {
            throw new ServiceValidationException("Cannot make changes to this transfer !!!");
        }
//        if(orgTransferRequest.getExpectedJoinDateEn() != null && LocalDate.now().compareTo(orgTransferRequest.getExpectedJoinDateEn()) < 0) {
//            throw new ServiceValidationException("Expected Join Date didnt meet");
//        }
    }

    @Override
    @Transactional
    public Long changeTransferAction(OrgTransferRequestPojo orgTransferRequestPojo) {
        OrgTransferRequest orgTransferRequest = orgTransferRequestRepo.findById(orgTransferRequestPojo.getId()).orElseThrow
                (() -> new NullPointerException("transfer not found"));
        EmployeeMinimalPojo employeeMinimalPojo = employeeService.employeeDetailMinimal(orgTransferRequest.getEmployeePisCode());
        if(orgTransferRequestPojo.getTransferStatus() != TransferStatus.P
                && orgTransferRequestPojo.getTransferStatus() != TransferStatus.C && orgTransferRequestPojo.getTransferStatus() != TransferStatus.R) {
            validApprove(orgTransferRequest);
        }

        if(orgTransferRequestPojo.getTransferStatus() == TransferStatus.A) {
            employeeService.updateEmployeeOffice(orgTransferRequest);
            employeeService.updateUserOffice(orgTransferRequest.getEmployeePisCode()
                    , orgTransferRequest.getTargetOffice().getCode(), orgTransferRequest.getRequestedOffice().getCode());
            this.acknowledgeApproveTransfer(orgTransferRequest.getEmployeePisCode()
                    , orgTransferRequest.getTargetOffice().getCode());
            sectionDesignationService.updateTransferProcess(Boolean.TRUE, orgTransferRequest.getEmployeePisCode());
        }
        orgTransferRequest.setTransferStatus(orgTransferRequestPojo.getTransferStatus());
        orgTransferRequest.setApprovedDateEn(LocalDate.now());
        orgTransferRequest.setApprovedDateNp(dateConverter.convertAdToBs(LocalDate.now().toString()));
        orgTransferRequest.setActive(Boolean.FALSE);
        orgTransferRequest = orgTransferRequestRepo.save(orgTransferRequest);
        addActionHistory(orgTransferRequest);
        orgTransferNotificationProducer.sendNotification(orgTransferRequest,
                orgTransferRequestPojo.getTransferStatus().toString());
        return orgTransferRequest.getId();
    }

    @Override
    public Page<OrgTransferRequestPojo> allFilteredTransferPaginated(GetRowsRequest paginatedRequest) {
        Page<OrgTransferRequestPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        return orgTransferRequestMapper.searchAllOfficeTransfersPaginated(page, paginatedRequest.getSearchField());

    }

    @Override
    public Page<OrgTransferRequestPojo> requestedTransferPaginated(GetRowsRequest paginatedRequest) {
        Page<OrgTransferRequestPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        return orgTransferRequestMapper.searchCurrentOfficeTransfersPaginated(page, paginatedRequest.getSearchField(), tokenProcessorService.getOfficeCode());
    }

    @Override
    public OrgTransferRequestPojo transferDetail(Long id) {
        return orgTransferRequestMapper.transferDetail(id);
    }

    @Override
    @Transactional
    public void acknowledgeTransfer(Long id, Long deviceId) {

        OrgTransferRequest orgTransferRequest = orgTransferRequestRepo.findById(id).orElseThrow
                (() -> new NullPointerException("transfer not found"));
        orgTransferRequest.setAcknowledged(Boolean.TRUE);
        if(orgTransferRequest.getTransferStatus().equals(TransferStatus.A)){
            this.validateDeviceId(orgTransferRequest.getEmployeePisCode(), deviceId);
            PisCodeToDeviceMapperPojo pisCodeToDeviceMapperPojo = new PisCodeToDeviceMapperPojo();
            pisCodeToDeviceMapperPojo.setDeviceID(deviceId);
            pisCodeToDeviceMapperPojo.setOfficeCode(orgTransferRequest.getTargetOffice().getCode());
            pisCodeToDeviceMapperPojo.setPisCode(orgTransferRequest.getEmployeePisCode());
            userService.saveDeviceId(pisCodeToDeviceMapperPojo);

            orgTransferNotificationProducer.sendNotification(orgTransferRequest,
                    TransferAction.AK.toString());

              } else {
            throw new ServiceValidationException("Invalid Action");
        }
         orgTransferRequestRepo.save(orgTransferRequest);
    }

    @Override
    public void update(OrgTransferRequestPojo orgTransferRequestPojo) {
        OrgTransferRequest orgTransferRequest = orgTransferRequestRepo.findById(orgTransferRequestPojo.getId()).orElseThrow
                (() -> new NullPointerException("transfer not found"));
        if(orgTransferRequest.getTransferStatus() != TransferStatus.P) {
            throw new ServiceValidationException(customMessageSource.getNepali("invalid.edit"));
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(orgTransferRequestPojo, orgTransferRequest);
        orgTransferRequest.setTargetOffice(new Office(orgTransferRequestPojo.getTargetOfficeCode()));
        orgTransferRequestRepo.save(orgTransferRequest);
    }

    //todo remove boilerplate code
    private void addActionHistory(OrgTransferRequest o) {

        OrgTransferHistory orgTransferHistory = new OrgTransferHistory();
        if (o.getTransferStatus().equals(TransferStatus.P)) {
            orgTransferHistory.setTransferStatus(TransferStatus.P);
            orgTransferHistory.setOrgTransferRequestId(o.getId());

            orgTransferHistory.setTransferType(o.getTransferType());
            orgTransferHistory.setExpectedJoinDateEn(o.getExpectedJoinDateEn());
            orgTransferHistory.setExpectedJoinDateNp(o.getExpectedJoinDateNp());

            orgTransferHistory.setFromOfficeCode(tokenProcessorService.getOfficeCode());
            orgTransferHistory.setTargetOfficeCode(o.getTargetOffice().getCode());
            orgTransferHistory.setFromSectionId(o.getFromSectionId());

            orgTransferHistory.setPisCode(o.getEmployeePisCode());
            orgTransferHistory.setRequestedDateEn(o.getRequestedDateEn());
            orgTransferHistory.setRequestedDateNp(o.getRequestedDateNp());

            orgTransferHistoryService.saveHistory(orgTransferHistory);
        } else {
            orgTransferHistoryService.updateHistoryStatus(o.getTransferStatus(), o.getId(),o.getAcknowledged(), o.getEmployeePisCode());
        }

    }

    private void validateTransfer(OrgTransferRequestPojo orgTransferRequestPojo){
        EmployeeMinimalPojo  employeeMinimalPojo = employeeService.employeeDetailMinimal(orgTransferRequestPojo.getEmployeePisCode());
        if(employeeMinimalPojo == null) {
            throw new ServiceValidationException("No Such Employee Found");
        }
        if(employeeMinimalPojo.getSection() != null && employeeMinimalPojo.getSection().getId() != null){
            orgTransferRequestPojo.setFromSectionId(employeeMinimalPojo.getSection().getId());
        }
        if(!ObjectUtils.isEmpty(orgTransferRequestMapper.pendingTransferOfEmployee(orgTransferRequestPojo.getEmployeePisCode()))){
            throw new ServiceValidationException("employee already has pending transfer");
        }
        if(delegationService.activeDelegation(employeeMinimalPojo.getPisCode())>0) {
            throw new ServiceValidationException("employee has active delegation period");
        }
        orgTransferRequestPojo.setFromSectionId(employeeMinimalPojo.getSectionId());
        GlobalApiResponse apiResponsePojo = attendanceServiceData.transferValidate(orgTransferRequestPojo.getEmployeePisCode(), orgTransferRequestPojo.getTargetOfficeCode());
        if(apiResponsePojo.getStatus() != ResponseStatus.SUCCESS){
            throw new ServiceValidationException(apiResponsePojo.getMessage());
        }

    }

    private void validateDeviceId(String pisCode, Long deviceId) {
        EmployeeMinimalPojo  employeeMinimalPojo = employeeService.employeeDetailMinimal(pisCode);
        if(deviceId == null && employeeMinimalPojo.getEmployeeServiceStatusCode() != null && !employeeMinimalPojo.getEmployeeServiceStatusCode().equals(EmployeeServiceStatusConstant.PERMANENT_EMPLOYEE_SERVICE_CODE)){
            throw new ServiceValidationException("Device id cannot be null for Karar/Unassigned employee");
        }
        if(deviceId != null && employeeMinimalPojo.getEmployeeServiceStatusCode() != null &&
           employeeMinimalPojo.getEmployeeServiceStatusCode().equals(EmployeeServiceStatusConstant.PERMANENT_EMPLOYEE_SERVICE_CODE)
           &&  !deviceId.toString().equals(pisCode)) {
                        throw new ServiceValidationException("Device id must be same as pisCode");
                    }
    }

    private void acknowledgeApproveTransfer(String pisCode, String officeCode){
        GlobalApiResponse apiResponsePojo = attendanceServiceData.transferEmployeeApprove(pisCode, officeCode);
        if(apiResponsePojo.getStatus() != ResponseStatus.SUCCESS){
            throw new ServiceValidationException(apiResponsePojo.getMessage());
        }

    }


}
