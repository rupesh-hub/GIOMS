package com.gerp.attendance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.shift.group.ShiftEmployeeGroupPojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.ShiftEmployeeGroupMapper;
import com.gerp.attendance.model.shift.group.ShiftEmployeeGroup;
import com.gerp.attendance.model.shift.group.ShiftEmployeeGroupMapping;
import com.gerp.attendance.repo.ShiftEmployeeGroupMappingRepo;
import com.gerp.attendance.repo.ShiftEmployeeGroupRepo;
import com.gerp.attendance.service.ShiftEmployeeGroupService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */

@Service
@Transactional
public class ShiftEmployeeGroupServiceImpl extends GenericServiceImpl<ShiftEmployeeGroup, Long> implements ShiftEmployeeGroupService {

    private final ShiftEmployeeGroupRepo shiftEmployeeGroupRepo;
    private final ShiftEmployeeGroupMapper shiftEmployeeGroupMapper;
    private final ShiftEmployeeGroupMappingRepo shiftEmployeeGroupMappingRepo;
    @Autowired private CustomMessageSource customMessageSource;
    @Autowired private UserMgmtServiceData userMgmtServiceData;
    @Autowired private TokenProcessorService tokenProcessorService;

    public ShiftEmployeeGroupServiceImpl(ShiftEmployeeGroupRepo shiftEmployeeGroupRepo,
                                         ShiftEmployeeGroupMappingRepo shiftEmployeeGroupMappingRepo,
                                         ShiftEmployeeGroupMapper shiftEmployeeGroupMapper){
        super(shiftEmployeeGroupRepo);
        this.shiftEmployeeGroupMapper = shiftEmployeeGroupMapper;
        this.shiftEmployeeGroupRepo = shiftEmployeeGroupRepo;
        this.shiftEmployeeGroupMappingRepo = shiftEmployeeGroupMappingRepo;
    }

    @Override
    public ShiftEmployeeGroup findById(Long id) {
        ShiftEmployeeGroup shiftEmployeeGroup = super.findById(id);
        if (shiftEmployeeGroup == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("shift")));
        return shiftEmployeeGroup;
    }


    @Override
    public ShiftEmployeeGroup create(ShiftEmployeeGroupPojo data) {
        IdNamePojo fiscalYear = userMgmtServiceData.findActiveFiscalYear();
        ShiftEmployeeGroup shiftEmployeeGroup = new ShiftEmployeeGroup().builder()
                .nameEn(data.getNameEn())
                .nameNp(data.getNameNp())
                .officeCode(tokenProcessorService.getOfficeCode())
                .employeeCount(Long.valueOf((data.getPisCodes()!=null&&!data.getPisCodes().isEmpty())?data.getPisCodes().size():0))
                .fiscalYear(fiscalYear.getId())
                .shiftEmployeeGroupMappings(
                        data.getPisCodes().stream().map(
                                x-> new ShiftEmployeeGroupMapping().builder()
                                        .pisCode(x)
                                        .build()
                        ).collect(Collectors.toList())
                )
                .build();
        shiftEmployeeGroupRepo.save(shiftEmployeeGroup);
        return shiftEmployeeGroup;
    }

    @Override
    public ShiftEmployeeGroup update(ShiftEmployeeGroupPojo data) {
        ShiftEmployeeGroup shiftEmployeeGroup = this.findById(data.getId());
        shiftEmployeeGroup.setNameEn(data.getNameEn());
        shiftEmployeeGroup.setNameNp(data.getNameNp());

        shiftEmployeeGroup.getShiftEmployeeGroupMappings().clear();
        List<ShiftEmployeeGroupMapping> shiftEmployeeGroupMappings =
                data.getPisCodes().stream().map(
                        x-> new ShiftEmployeeGroupMapping().builder()
                                .pisCode(x)
                                .build()
                ).collect(Collectors.toList());
        shiftEmployeeGroup.getShiftEmployeeGroupMappings().addAll(shiftEmployeeGroupMappings);
        shiftEmployeeGroup.setEmployeeCount(
                Long.valueOf((data.getPisCodes()!=null&&!data.getPisCodes().isEmpty())?data.getPisCodes().size():0)
        );
        shiftEmployeeGroupRepo.save(shiftEmployeeGroup);
        return shiftEmployeeGroup;
    }

    @Override
    public ShiftEmployeeGroupPojo findByIdCustom(Long id) {
        ShiftEmployeeGroupPojo data = shiftEmployeeGroupMapper.findById(id);
        List<String> disabledPisCode = new ArrayList<>();
        data.setPisCodes(data.getEmployees().stream().map(x->x.getPisCode()).collect(Collectors.toList()));
        if(data.getPisCodes()!=null && !data.getPisCodes().isEmpty())
            disabledPisCode.addAll(shiftEmployeeGroupMapper.getMappedPisCodeByOfficeWithCheckedFilter(tokenProcessorService.getOfficeCode(), data.getPisCodes()));
        else
            disabledPisCode.addAll(shiftEmployeeGroupMapper.getMappedPisCodeByOffice(tokenProcessorService.getOfficeCode()));
        data.getEmployees().forEach(x->{
            EmployeeMinimalPojo minimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(x.getPisCode());
            x.setEmployeeNameEn(minimalPojo.getEmployeeNameEn());
            x.setEmployeeNameNp(minimalPojo.getEmployeeNameNp());
            x.setCoreDesignation(minimalPojo.getCoreDesignation());
            x.setFunctionalDesignation(minimalPojo.getFunctionalDesignation());
        });
        data.setDisabledPisCodes(disabledPisCode);
        return data;
    }

    @Override
    public List<ShiftEmployeeGroupPojo> getAllCustomEntity(Long fiscalYear) {
        return shiftEmployeeGroupMapper.findAllByFiscalYear(fiscalYear);
    }

    @Override
    public List<ShiftEmployeeGroupPojo> getAllCustom() {
        return shiftEmployeeGroupMapper.findAll(tokenProcessorService.getOfficeCode());
    }

    @Override
    public List<String> getUsedPisCodeForOffice() {
        return shiftEmployeeGroupMapper.getMappedPisCodeByOffice(tokenProcessorService.getOfficeCode());
    }

    @Override
    public Page<ShiftEmployeeGroupPojo> getAllByFiscalYear(GetRowsRequest paginatedRequest) {
        Page<ShiftEmployeeGroupPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        page = shiftEmployeeGroupMapper.findAllByFiscalYearPaginated(
                page,
                paginatedRequest.getFiscalYear(),
                paginatedRequest.getSearchField()
        );
        return page;
    }

    @Override
    public void deleteById(Long id) {
        ShiftEmployeeGroup shiftEmployeeGroup = this.findById(id);

        super.delete(shiftEmployeeGroup);
    }
}
