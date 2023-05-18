package com.gerp.dartachalani.service.impl;

import com.gerp.dartachalani.Proxy.UserMgmtServiceData;
import com.gerp.dartachalani.converter.DtoConverter;
import com.gerp.dartachalani.dto.DraftShareDto;
import com.gerp.dartachalani.dto.DraftShareLogPojo;
import com.gerp.dartachalani.dto.DraftSharePojo;
import com.gerp.dartachalani.dto.EmployeePojo;
import com.gerp.dartachalani.dto.enums.DcTablesEnum;
import com.gerp.dartachalani.mapper.DraftShareLogMapper;
import com.gerp.dartachalani.mapper.DraftShareMapper;
import com.gerp.dartachalani.model.dispatch.DispatchLetter;
import com.gerp.dartachalani.model.draft.share.DraftShare;
import com.gerp.dartachalani.model.draft.share.DraftShareLog;
import com.gerp.dartachalani.model.memo.Memo;
import com.gerp.dartachalani.repo.DispatchLetterRepo;
import com.gerp.dartachalani.repo.DraftShareLogRepo;
import com.gerp.dartachalani.repo.DraftShareRepo;
import com.gerp.dartachalani.repo.MemoRepo;
import com.gerp.dartachalani.service.draft.share.DraftShareService;
import com.gerp.dartachalani.token.TokenProcessorService;
import com.gerp.shared.enums.Status;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DraftShareServiceImpl implements DraftShareService {

    private final TokenProcessorService tokenProcessorService;
    private final UserMgmtServiceData userMgmtServiceData;
    private final DraftShareRepo draftShareRepo;
    private final DraftShareMapper draftShareMapper;
    private final DispatchLetterRepo dispatchLetterRepo;
    private final MemoRepo memoRepo;
    private final DraftShareLogRepo draftShareLogRepo;
    private final DraftShareLogMapper draftShareLogMapper;

    @Autowired
    private DateConverter dateConverter;

    public DraftShareServiceImpl(TokenProcessorService tokenProcessorService, UserMgmtServiceData userMgmtServiceData, DraftShareRepo draftShareRepo,
                                 DraftShareMapper draftShareMapper, DispatchLetterRepo dispatchLetterRepo, MemoRepo memoRepo, DraftShareLogRepo draftShareLogRepo,
                                 DraftShareLogMapper draftShareLogMapper) {
        this.tokenProcessorService = tokenProcessorService;
        this.userMgmtServiceData = userMgmtServiceData;
        this.draftShareRepo = draftShareRepo;
        this.draftShareMapper = draftShareMapper;
        this.dispatchLetterRepo = dispatchLetterRepo;
        this.memoRepo = memoRepo;
        this.draftShareLogRepo = draftShareLogRepo;
        this.draftShareLogMapper = draftShareLogMapper;
    }

    @Override
    public boolean share(DraftShareDto draftShareDto) {

        if (draftShareDto.getLetterType() == DcTablesEnum.DISPATCH) {
            DispatchLetter dispatchLetter = dispatchLetterRepo.findById(draftShareDto.getDispatchId()).orElseThrow(() -> new RuntimeException("Letter not found with id: " + draftShareDto.getDispatchId()));
            if (!dispatchLetter.getIsDraft())
                throw new RuntimeException("Letter must be drafted to share");

            if (draftShareRepo.existsByReceiverPisCodeAndReceiverSectionCodeAndDispatchIdAndIsActiveAndLetterType
                    (draftShareDto.getReceiverPisCode(), draftShareDto.getReceiverSectionCode(), draftShareDto.getDispatchId(), Boolean.TRUE, DcTablesEnum.DISPATCH))
                throw new RuntimeException("Already shared to this user");

        } else if (draftShareDto.getLetterType() == DcTablesEnum.MEMO) {
            Memo memo = memoRepo.findById(draftShareDto.getMemoId()).orElseThrow(() -> new RuntimeException(""));
            if (!memo.getIsDraft())
                throw new RuntimeException("Must be drafted to share");

            if (draftShareRepo.existsByReceiverPisCodeAndReceiverSectionCodeAndMemoIdAndIsActiveAndLetterType
                    (draftShareDto.getReceiverPisCode(), draftShareDto.getReceiverSectionCode(), draftShareDto.getMemoId(), Boolean.TRUE, DcTablesEnum.MEMO))
                throw new RuntimeException("Already shared to this user");
        } else {
            throw new RuntimeException("Invalid letter type");
        }

        DraftShare draftShare = DtoConverter.convert(draftShareDto);

        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(tokenPisCode);

        //check user section
        checkUserSection(employeeMinimalPojo);

        String tokenSectionCode = employeeMinimalPojo.getSection().getId().toString();

        draftShare.setSenderPisCode(tokenPisCode);
        draftShare.setSenderSectionCode(tokenSectionCode);
        draftShareRepo.save(draftShare);

        return true;
    }

    @Override
    public boolean update(DraftShareDto draftShareDto) {

        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeeMinimalPojo employeePojo = userMgmtServiceData.getEmployeeDetailMinimal(tokenPisCode);

        //check user section
        checkUserSection(employeePojo);

        String tokenUserSectionCode = employeePojo.getSection().getId().toString();

        //share draft
        Optional<DraftShare> dispatchLetterDraftShareOp =
                draftShareDto.getLetterType() == DcTablesEnum.DISPATCH ?
                        draftShareRepo.getByReceiverPisCodeAndReceiverSectionCodeAndDispatchIdAndIsActiveAndLetterType(tokenPisCode, tokenUserSectionCode, draftShareDto.getDispatchId(), Boolean.TRUE, DcTablesEnum.DISPATCH)
                        : draftShareDto.getLetterType() == DcTablesEnum.MEMO ?
                        draftShareRepo.getByReceiverPisCodeAndReceiverSectionCodeAndMemoIdAndIsActiveAndLetterType(tokenPisCode, tokenUserSectionCode, draftShareDto.getMemoId(), Boolean.TRUE, DcTablesEnum.MEMO) : Optional.empty();
        if (dispatchLetterDraftShareOp.isPresent()) {
            DraftShare draftShare = dispatchLetterDraftShareOp.get();
            draftShare.setStatus(draftShareDto.getStatus());
            draftShareRepo.save(draftShare);

            DraftShareLog draftShareLog = DraftShareLog.builder()
                    .fromStatus(draftShare.getStatus())
                    .toStatus(draftShareDto.getStatus())
                    .pisCode(tokenPisCode)
                    .sectionCode(tokenUserSectionCode)
                    .draftShareId(draftShare.getId())
                    .build();

            draftShareLogRepo.save(draftShareLog);
            return true;
        }
        return false;
    }

    @Override
    public List<DraftSharePojo> getDraftShareList(Long dispatchId, Long memoId) {
        if (dispatchId == null && memoId == null)
            throw new RuntimeException("Letter id is required");

        List<DraftSharePojo> list = draftShareMapper.getDraftShareList(dispatchId, memoId);
        if (list != null && !list.isEmpty()) {
            list.forEach(x -> {
                x.setLastModifiedDateBs(dateConverter.convertAdToBs(x.getLastModifiedDate().toLocalDateTime().toLocalDate().toString()));
                x.setLastModifiedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(x.getLastModifiedDate().toLocalDateTime().toLocalDate().toString())));
                x.setReceiverDetail(userMgmtServiceData.getEmployeeDetailMinimal(x.getReceiverPisCode()));
                x.setSenderDetail(userMgmtServiceData.getEmployeeDetailMinimal(x.getSenderPisCode()));
            });
        }
        return list;
    }

    @Override
    public List<DraftSharePojo> getDraftShareLog(Long dispatchId, Long memoId) {
        if (dispatchId == null && memoId == null)
            throw new RuntimeException("Letter id is required");
        List<DraftSharePojo> list = draftShareLogMapper.getDraftShareLog(dispatchId, memoId);
        if (list != null && !list.isEmpty()) {
            list.forEach(x -> {
                x.setLastModifiedDateBs(dateConverter.convertAdToBs(x.getLastModifiedDate().toLocalDateTime().toLocalDate().toString()));
                x.setLastModifiedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(x.getLastModifiedDate().toLocalDateTime().toLocalDate().toString())));
                if (x.getSenderPisCode() != null)
                    x.setSenderDetail(userMgmtServiceData.getEmployeeDetailMinimal(x.getSenderPisCode()));
                if (x.getReceiverPisCode() != null)
                    x.setReceiverDetail(userMgmtServiceData.getEmployeeDetailMinimal(x.getReceiverPisCode()));
            });
        }

        return list;
    }

    @Override
    @Transactional
    public boolean removeEmployee(String pisCode, String sectionCode, Long dispatchId, Long memoId) {
        if (dispatchId != null)
            draftShareRepo.removeDispatchDraftShareEmployee(pisCode, sectionCode, dispatchId);
        if (memoId != null)
            draftShareRepo.removeMemoDraftShareEmployee(pisCode, sectionCode, memoId);
        return true;
    }

    //this function check the user is involved in any section or not
    private void checkUserSection(EmployeeMinimalPojo employeePojo) {
        if (employeePojo == null)
            throw new RuntimeException("Employee detail not found with id : " + tokenProcessorService.getPisCode());

        if (employeePojo.getSection() == null)
            throw new RuntimeException("प्रयोगकर्ताको शाखा फेला नपरेको हुनाले यस पत्रमा कार्य गर्न अवरोध गरिएको छ");
    }
}
