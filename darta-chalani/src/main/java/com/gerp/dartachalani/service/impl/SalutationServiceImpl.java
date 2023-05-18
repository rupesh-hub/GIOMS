package com.gerp.dartachalani.service.impl;

import com.gerp.dartachalani.Proxy.UserMgmtServiceData;
import com.gerp.dartachalani.dto.EmployeePojo;
import com.gerp.dartachalani.dto.SalutationPojo;
import com.gerp.dartachalani.dto.SectionPojo;
import com.gerp.dartachalani.mapper.SalutationMapper;
import com.gerp.dartachalani.model.Salutation;
import com.gerp.dartachalani.repo.SalutationRepo;
import com.gerp.dartachalani.service.SalutationService;
import com.gerp.dartachalani.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.utils.CrudMessages;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class SalutationServiceImpl implements SalutationService {
    private final TokenProcessorService tokenProcessorService;
    private final SalutationRepo salutationRepo;
    private final CustomMessageSource customMessageSource;
    private final SalutationMapper salutationMapper;
    private final UserMgmtServiceData userMgmtServiceData;

    public SalutationServiceImpl(TokenProcessorService tokenProcessorService, SalutationRepo salutationRepo, CustomMessageSource customMessageSource, SalutationMapper salutationMapper, UserMgmtServiceData userMgmtServiceData) {
        this.tokenProcessorService = tokenProcessorService;
        this.salutationRepo = salutationRepo;
        this.customMessageSource = customMessageSource;
        this.salutationMapper = salutationMapper;
        this.userMgmtServiceData = userMgmtServiceData;
    }

    @Override
    public Long addSalutation(SalutationPojo dto) {
        Salutation salutation = new Salutation();
        copyObject(dto, salutation);
        return salutationRepo.save(salutation).getId();
    }

    @Override
    public List<SalutationPojo> getSalutation(String pisCode) {

        List<SalutationPojo> salutationList = salutationMapper.findByCreator(tokenProcessorService.getPisCode(), pisCode);

        salutationList.stream().forEach(data -> {
            if (data.getPisCode() != null) {
                EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(data.getPisCode());
                if (employeePojo != null) {
                    EmployeePojo employeePojo1 = new EmployeePojo();
                    employeePojo1.setPisCode(employeePojo.getPisCode());
                    employeePojo1.setNameNp(employeePojo.getNameNp());
                    employeePojo1.setNameEn(employeePojo.getNameEn());
                    data.setEmployee(employeePojo1);
                }
            }
            if (data.getSectionId() != 0) {
                System.out.println("section id: "+data.getSectionId());
                SectionPojo sectionPojo = userMgmtServiceData.getSectionDetail(Long.valueOf(data.getSectionId()));
                SectionPojo sectionPojo1 = new SectionPojo();
                sectionPojo1.setNameEn(sectionPojo != null? sectionPojo.getNameEn() : "");
                sectionPojo1.setNameNp(sectionPojo!= null ? sectionPojo.getNameNp() : "");

                data.setSection(sectionPojo1);
            }
            if (data.getOfficeCode() != null && !data.getOfficeCode().equals("")) {
                data.setOffice(userMgmtServiceData.getOfficeDetail(data.getOfficeCode()));
            }
        });

        return salutationList;
    }

    @Override
    public Long updateSalutation(SalutationPojo dto) {
        Optional<Salutation> salutationOptional = salutationRepo.findById(dto.getId());
        if (!salutationOptional.isPresent()) {
            throw new CustomException(customMessageSource.get(CrudMessages.notExist, "Salutation"));
        }
        Salutation salutation = salutationOptional.orElse(new Salutation());
        copyObject(dto, salutation);
        return salutationRepo.save(salutation).getId();
    }

    @Override
    public Long deleteSalutation(Long id) {
        salutationRepo.deleteById(id);
        return id;
    }

    private void copyObject(SalutationPojo dto, Salutation salutation) {
        salutation.setCreator(tokenProcessorService.getPisCode());
        salutation.setPisCode(dto.getPisCode());
        salutation.setCustomSalutationEn(dto.getCustomSalutationEn());
        salutation.setSectionId(dto.getSectionId());
        salutation.setType(dto.getType());
        salutation.setCustomSalutationNp(dto.getCustomSalutationNp());
        salutation.setOfficeCode(dto.getOfficeCode());
    }
}
