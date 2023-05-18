package com.gerp.usermgmt.services.transfer.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.converter.transfer.TransferRequestConverter;
import com.gerp.usermgmt.mapper.transfer.TransferMapper;
import com.gerp.usermgmt.mapper.transfer.TransferRequestMapper;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.model.transfer.PreviousWorkDetails;
import com.gerp.usermgmt.model.transfer.TransferRequest;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;
import com.gerp.usermgmt.pojo.transfer.*;
import com.gerp.usermgmt.repo.employee.EmployeeRepo;
import com.gerp.usermgmt.repo.transfer.TransferRequestRepo;
import com.gerp.usermgmt.services.organization.office.OfficeService;
import com.gerp.usermgmt.services.organization.office.impl.OfficeServiceImpl;
import com.gerp.usermgmt.services.transfer.TransferRequestService;
import com.gerp.usermgmt.services.transfer.TransferService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransferRequestServiceImpl implements TransferRequestService {
   private final TransferRequestConverter transferRequestConverter;
   private final TransferRequestRepo transferRequestRepo;
   private final TransferRequestMapper transferRequestMapper;
   private final TokenProcessorService tokenProcessorService;
   private final TransferService transferService;
   private final CustomMessageSource customMessageSource;
   private final EmployeeRepo employeeRepo;
   private final OfficeService officeService;
   private final TransferMapper transferMapper;

    public TransferRequestServiceImpl(TransferRequestConverter transferRequestConverter, TransferRequestRepo transferRequestRepo, TransferRequestMapper transferRequestMapper, TokenProcessorService tokenProcessorService, TransferService transferService, CustomMessageSource customMessageSource, EmployeeRepo employeeRepo, OfficeService officeService, TransferMapper transferMapper) {
        this.transferRequestConverter = transferRequestConverter;
        this.transferRequestRepo = transferRequestRepo;
        this.transferRequestMapper = transferRequestMapper;
        this.tokenProcessorService = tokenProcessorService;
        this.transferService = transferService;
        this.customMessageSource = customMessageSource;
        this.employeeRepo = employeeRepo;
        this.officeService = officeService;
        this.transferMapper = transferMapper;
    }

    @Override
    public Long addTransferRequest(TransferRequestPojo transferRequestPojo) {
       TransferRequest transferRequest = transferRequestConverter.toEntity(transferRequestPojo);
       return transferRequestRepo.save(transferRequest).getId();
    }

    @Override
    public List<TransferResponsePojo> getTransferRequest(Long id) {
        return transferRequestMapper.getTransferRequest( id);
    }

    @Override
    public Page<TransferRequestForOfficePojo> getTransferRequestToOffice(int limit, int page) {
        Page<TransferRequestForOfficePojo> transferRequestForOfficePojoPage = new Page<>(page,limit);
        transferRequestForOfficePojoPage=  transferRequestMapper.getTransferRequestToOffice(transferRequestForOfficePojoPage,tokenProcessorService.getOfficeCode());
        transferRequestForOfficePojoPage.getRecords().parallelStream().forEach(obj->{
            String officeCode =  obj.getEmployee().getJoinedDateNp();
            if (officeCode != null){
                 obj.getEmployee().setOfficeLevels(officeService.findByCode(officeCode));
            }
            obj.getEmployee().setJoinedDateNp(null);
           obj.getEmployee().setCurrentService(getService(obj.getEmployee().getCurrentService().getCode(),obj.getEmployee().getCurrentService(),1));
        });
        return transferRequestForOfficePojoPage  ;
    }

    private DetailPojo getService(String code,DetailPojo serviceLevel,int count) {
        ServicePojo service = transferMapper.getService(code);
        if (count >1){
            DetailPojo detailPojo = new DetailPojo();
            detailPojo.setCode(service.getCode());
            detailPojo.setNameEn(service.getNameEn());
            detailPojo.setNameNp(service.getNameNp());
            serviceLevel.setCurrentService(detailPojo);
        }else {
            serviceLevel.setCode(service.getCode());
            serviceLevel.setNameEn(service.getNameEn());
            serviceLevel.setNameNp(service.getNameNp());
        }
        if (service.getParentCode() == null || String.valueOf(service.getParentCode()).equals("142")){
            return serviceLevel;
        }
        count ++;
        if (count >2){
            getService(service.getParentCode()+"",serviceLevel.getCurrentService(),count);
        }else {
            getService(service.getParentCode()+"",serviceLevel,count);
        }
        return serviceLevel;
    }
    @Override
    public List<TransferResponsePojo> getTransferSelfCreated() {
        return transferRequestMapper.getTransferCreatedBySelf(tokenProcessorService.getPisCode());
    }

    @Override
    public Long addTransferRequestToTransfer(List<TransferRequestToTransferPojo> dto) {
        dto.parallelStream().forEach(obj->{
            Optional<TransferRequest> requestOptional = transferRequestRepo.findById(obj.getTransferId());
            if (!requestOptional.isPresent()){
                throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"Transfer Request"));
            }
            TransferRequest transferRequest = requestOptional.orElse(new TransferRequest());
            Employee employee = employeeRepo.findByPisCode(transferRequest.getEmployeePsCode());
            TransferPojo transferPojo ;
            List<PreviousWorkDetails> previousWorkDetails = transferRequest.getPreviousWorkDetailsList().parallelStream().filter(obj1 -> obj1.getOfficeCode().equalsIgnoreCase(employee.getOffice().getCode())).collect(Collectors.toList());
            if (previousWorkDetails.size() != 0){
                PreviousWorkDetails previousWorkDetail = previousWorkDetails.get(0);
                transferPojo= new TransferPojo(previousWorkDetail.getOfficeCode(),previousWorkDetail.getServiceCode(),
                        previousWorkDetail.getPositionCode(),previousWorkDetail.getDesignationCode(),
                        obj.getToOfficeCode(),obj.getToServiceCode(),obj.getToPositionCode(),obj.getToGroupCode(),
                        obj.getToSubGroupCode(),obj.getToDesignationCode(),transferRequest.getEmployeePsCode(),obj.getExpectedDepartureDateNp(),obj.getExpectedDepartureDateEn(),"internal");
            }else {
                transferPojo= new TransferPojo(employee.getOffice().getCode(),employee.getService().getCode(),
                        employee.getPosition().getCode(),employee.getDesignation().getCode(),
                        obj.getToOfficeCode(),obj.getToServiceCode(),obj.getToPositionCode(),obj.getToGroupCode(),
                        obj.getToSubGroupCode(),obj.getToDesignationCode(),transferRequest.getEmployeePsCode(),obj.getExpectedDepartureDateNp(),obj.getExpectedDepartureDateEn(),"internal");
            }
            List<TransferPojo> transferPojoSet = new ArrayList<>();
            transferPojoSet.add(transferPojo);
            transferService.addTransfer(transferPojoSet);
            transferRequest.setIsSubmitted(true);
            transferRequestRepo.save(transferRequest);
        });
        return dto.get(0).getTransferId();
    }

//    @Override
//    public TransferResponsePojo getTransferRequestDetailMini(Long id) {
//        return transferRequestMapper.getTransferRequestDetailMini(id);
//    }
}
