package com.gerp.usermgmt.services.organization.fiscalyear;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.pojo.FiscalYearPojo;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.model.fiscalyear.FiscalYear;
import com.gerp.usermgmt.repo.fiscalyear.FiscalYearRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FiscalYearServiceImpl extends GenericServiceImpl<FiscalYear, String> implements FiscalYearService {
    private final FiscalYearRepo fiscalYearRepo;
    private final CustomMessageSource customMessageSource;

    public FiscalYearServiceImpl(FiscalYearRepo repository, CustomMessageSource customMessageSource) {
        super(repository);
        this.fiscalYearRepo = repository;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public FiscalYear getActiveYear() {
        return fiscalYearRepo.findActiveYear();
    }

    @Override
    public List<IdNamePojo> getALlYear() {
        List<FiscalYear> years = this.findAllWithInactive();
        return years.stream().map(fiscalYear -> new IdNamePojo().builder()
                .code(fiscalYear.getCode())
                .name(fiscalYear.getYear())
                .nameN(fiscalYear.getYearNp())
                .build()).collect(Collectors.toList());
    }

    @Override
    public FiscalYearPojo getFiscalYearsDetails(String fiscalYear) {
        FiscalYear byYear = fiscalYearRepo.findByYear(fiscalYear);
        return FiscalYearPojo.builder()
                .name(byYear.getYear())
                .nameNp(byYear.getYearNp())
                .code(byYear.getCode())
                .startDate(byYear.getStartDate())
                .endDate(byYear.getEndDate())
                .build();
    }

    @Override
    public FiscalYearPojo getFiscalYearsByCode(String fiscalYearCode) {
        FiscalYear byYear = fiscalYearRepo.findByYearCode(fiscalYearCode);
        if (byYear != null) {
            return FiscalYearPojo.builder()
                    .name(byYear.getYear())
                    .nameNp(byYear.getYearNp())
                    .code(byYear.getCode())
                    .startDate(byYear.getStartDate())
                    .endDate(byYear.getEndDate())
                    .build();
        } else {
            return null;
        }
    }
}
