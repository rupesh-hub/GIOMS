package com.gerp.usermgmt.services.organization.office.impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.converter.organiztion.office.OfficeSetupConverter;
import com.gerp.usermgmt.enums.OfficeSetupStepEnum;
import com.gerp.usermgmt.model.office.OfficeSetup;
import com.gerp.usermgmt.pojo.organization.office.OfficeSetupPojo;
import com.gerp.usermgmt.repo.office.OfficeSetupRepo;
import com.gerp.usermgmt.services.organization.office.OfficeSetupService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfficeSetupServiceImpl extends GenericServiceImpl<OfficeSetup, Long> implements OfficeSetupService {

    private OfficeSetupRepo officeSetupRepo;

    private OfficeSetupConverter officeSetupConverter;

    private TokenProcessorService tokenProcessorService;

    private CustomMessageSource customMessageSource;

    public OfficeSetupServiceImpl(OfficeSetupRepo officeSetupRepo,
                                  OfficeSetupConverter officeSetupConverter,
                                  TokenProcessorService tokenProcessorService,
                                  CustomMessageSource messageSource) {
        super(officeSetupRepo);
        this.officeSetupRepo = officeSetupRepo;
        this.officeSetupConverter = officeSetupConverter;
        this.tokenProcessorService = tokenProcessorService;
        this.customMessageSource = messageSource;
    }

    @Override
    public OfficeSetupPojo findStepStatus(String step) {
        String officeCode = tokenProcessorService.getOfficeCode();
        if(officeCode ==null) throw  new CustomException(customMessageSource.get("not.found", "office"));
        OfficeSetup officeSetup = officeSetupRepo.findByStepAndOfficeCode(OfficeSetupStepEnum.valueOf(step), officeCode);
        if(officeSetup ==null) {
            return null;
        }
      return officeSetupConverter.toPojo(officeSetup);
    }

    @Override
    public Long updateOfficeSetup(OfficeSetupPojo officeSetupPojo) {
            String officeCode = tokenProcessorService.getOfficeCode();
            if(officeCode ==null) throw  new CustomException(customMessageSource.get("not.found", "office"));
            if(officeSetupPojo.getOfficeSetupStatus() ==null && officeSetupPojo.getOfficeSetupStep() ==null) throw new CustomException(customMessageSource.get("not.found", "Required field"));
        officeSetupPojo.setOfficeCode(officeCode);
        OfficeSetup officeSetup = officeSetupRepo.findByStepAndOfficeCode(officeSetupPojo.getOfficeSetupStep(), officeCode);
            if(officeSetup ==null){
                officeSetup = officeSetupConverter.toModel(officeSetupPojo);
            } else {
                officeSetup.setStepStatus(officeSetupPojo.getOfficeSetupStatus());
            }
        return officeSetupRepo.save(officeSetup).getId();
    }

    @Override
    public List<OfficeSetupPojo> findAllOfficeStepStatus() {

        String officeCode = tokenProcessorService.getOfficeCode();
        if(officeCode ==null) throw  new CustomException(customMessageSource.get("not.found", "office"));
        List<OfficeSetup> officeSetups = officeSetupRepo.findAllByOfficeCode(officeCode);
        if(officeSetups == null) return null;
        List<OfficeSetupPojo> officeSetupPojos = officeSetups.stream().map(officeSetup -> {
            return officeSetupConverter.toPojo(officeSetup);
        }).collect(Collectors.toList());
        return officeSetupPojos;

    }
}
