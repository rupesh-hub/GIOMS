package com.gerp.usermgmt.services.orgtransfer.impl;

import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.gerp.usermgmt.enums.TransferStatus;
import com.gerp.usermgmt.mapper.organization.EmployeeMapper;
import com.gerp.usermgmt.mapper.organization.orgtransfer.OrgTransferHistoryMapper;
import com.gerp.usermgmt.model.orgtransfer.OrgTransferHistory;
import com.gerp.usermgmt.pojo.organization.orgtransfer.OrgTransferHistoryPojo;
import com.gerp.usermgmt.repo.orgtransfer.OrgTransferHistoryRepo;
import com.gerp.usermgmt.services.orgtransfer.OrgTransferHistoryService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrgTransferHistoryServiceImpl implements OrgTransferHistoryService {
    private final OrgTransferHistoryRepo orgTransferHistoryRepo;
    private final OrgTransferHistoryMapper orgTransferHistoryMapper;
    private final DateConverter dateConverter;
    private final TokenProcessorService tokenProcessorService;

    // todo refactor
    private final EmployeeMapper employeeMapper;

    public OrgTransferHistoryServiceImpl(OrgTransferHistoryRepo orgTransferHistoryRepo, OrgTransferHistoryMapper orgTransferHistoryMapper, DateConverter dateConverter, TokenProcessorService tokenProcessorService, EmployeeMapper employeeMapper) {
        this.orgTransferHistoryRepo = orgTransferHistoryRepo;
        this.orgTransferHistoryMapper = orgTransferHistoryMapper;
        this.dateConverter = dateConverter;
        this.tokenProcessorService = tokenProcessorService;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public void saveHistory(OrgTransferHistory orgTransferHistory) {
        orgTransferHistoryRepo.save(orgTransferHistory);
    }

    @Override
    public void updateHistoryStatus(TransferStatus transferStatus, Long transferId, Boolean acknowledged, String employeePisCode) {
        OrgTransferHistory orgTransferHistory = orgTransferHistoryRepo.findOrgTransferHistoryByOrgTransferRequestId(transferId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT,"No Transfer Request found"));
        orgTransferHistory.setTransferStatus(transferStatus);
        orgTransferHistory.setActive(Boolean.FALSE);
        if(transferStatus.equals(TransferStatus.A)){
            EmployeeMinimalPojo employeeMinimalPojo = employeeMapper.getByCodeMinimal(employeePisCode);
            orgTransferHistory.setAcknowledged(acknowledged);
            orgTransferHistory.setPrevOfficeJoinDateEn(employeeMinimalPojo.getCurOfficeJoinDtEn());
            orgTransferHistory.setPrevOfficeJoinDateEn(employeeMinimalPojo.getCurOfficeJoinDtNp());
        }
        orgTransferHistory.setApproverCode(tokenProcessorService.getPisCode());
        orgTransferHistory.setApprovedDateEn(LocalDate.now());
        orgTransferHistory.setApprovedDateNp(dateConverter.convertAdToBs(LocalDate.now().toString()));
        orgTransferHistoryRepo.save(orgTransferHistory);
    }

    @Override
    public List<OrgTransferHistoryPojo> transferHistory(String pisCode) {
        return orgTransferHistoryMapper.transferHistoryByPisCode(pisCode);
    }

    @Override
    public List<Map<String, Object>> getTransferHistory(String pisCode) {
        List<Map<String, Object>> historyMapList = new ArrayList<>();
        String officeCode = tokenProcessorService.getOfficeCode();
        if (tokenProcessorService.getIsOfficeHead() || tokenProcessorService.isOfficeAdmin()) {
            historyMapList = orgTransferHistoryRepo.transferPreAndAfter(pisCode, pisCode, officeCode);
        } else if (tokenProcessorService.isUser() || tokenProcessorService.isAdmin()) {
            historyMapList = orgTransferHistoryRepo.getTransferHistory(pisCode);
        } else {
            throw new ServiceValidationException("Not authorized to fetch data");
        }
        return historyMapList;
    }
}
