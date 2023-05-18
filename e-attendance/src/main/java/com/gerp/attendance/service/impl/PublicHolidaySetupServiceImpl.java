package com.gerp.attendance.service.impl;

import com.gerp.attendance.Pojo.HolidayMapperPojo;
import com.gerp.attendance.Pojo.PublicHolidaySetupPojo;
import com.gerp.attendance.mapper.PublicHolidayMapper;
import com.gerp.attendance.model.setup.PublicHolidaySetup;
import com.gerp.attendance.repo.PublicHolidaySetupRepo;
import com.gerp.attendance.service.PublicHolidaySetupService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class PublicHolidaySetupServiceImpl extends GenericServiceImpl<PublicHolidaySetup, Integer> implements PublicHolidaySetupService {
    private final PublicHolidaySetupRepo publicHolidaySetupRepo;
    private final CustomMessageSource customMessageSource;
    private final PublicHolidayMapper publicHolidayMapper;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    public PublicHolidaySetupServiceImpl(PublicHolidaySetupRepo publicHolidaySetupRepo,
                                         CustomMessageSource customMessageSource,
                                         PublicHolidayMapper publicHolidayMapper) {
        super(publicHolidaySetupRepo);
        this.publicHolidaySetupRepo = publicHolidaySetupRepo;
        this.customMessageSource = customMessageSource;
        this.publicHolidayMapper = publicHolidayMapper;
    }

    private PublicHolidaySetup PublicHolidayDtoEntity(PublicHolidaySetupPojo publicHolidaySetupPojo) {
        String officeCode = tokenProcessorService.getOfficeCode();

        PublicHolidaySetup publicHolidaySetup=new PublicHolidaySetup();
        publicHolidaySetup.setNameEn(publicHolidaySetupPojo.getNameEn());
        publicHolidaySetup.setNameNp(publicHolidaySetupPojo.getNameNp());
        publicHolidaySetup.setShortNameEn(publicHolidaySetupPojo.getShortNameEn());
        publicHolidaySetup.setShortNameNp(publicHolidaySetupPojo.getShortNameNp());
        publicHolidaySetup.setHolidayFor(publicHolidaySetupPojo.getHolidayFor());
        publicHolidaySetup.setOfficeCode(officeCode);
        return publicHolidaySetup;
    }

    @Override
    public PublicHolidaySetup findById(Integer uuid) {
        PublicHolidaySetup publicHolidaySetup = super.findById(uuid);
        if (publicHolidaySetup == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("public.holiday.setup")));
        return publicHolidaySetup;
    }

    @Override
    public PublicHolidaySetup save(PublicHolidaySetupPojo publicHolidaySetupPojo) {
        PublicHolidaySetup publicHolidaySetup = PublicHolidayDtoEntity(publicHolidaySetupPojo);
        publicHolidaySetupRepo.save(publicHolidaySetup);
        return publicHolidaySetup;
    }

    @Override
    public ArrayList<HolidayMapperPojo> getAllHolidays() {
        return publicHolidayMapper.getAllHolidays();
    }

    @Override
    public HolidayMapperPojo getHolidayById(Integer id) {
        return publicHolidayMapper.getHolidayById(id);    }

    @Override
    public ArrayList<HolidayMapperPojo> getByOfficeCode(String officeCode) {
        return publicHolidayMapper.getByOfficeCode(officeCode);
    }

    @Override
    public void updatePublicHoliday(PublicHolidaySetupPojo publicHolidaySetupPojo) {
        PublicHolidaySetup update = publicHolidaySetupRepo.findById(publicHolidaySetupPojo.getId()).get();

        PublicHolidaySetup publicHolidaySetup = new PublicHolidaySetup().builder()
                .nameEn(publicHolidaySetupPojo.getNameEn())
                .nameNp(publicHolidaySetupPojo.getNameNp())
                .shortNameEn(publicHolidaySetupPojo.getShortNameEn())
                .shortNameNp(publicHolidaySetupPojo.getShortNameNp())
                .holidayFor(publicHolidaySetupPojo.getHolidayFor())
                .officeCode(publicHolidaySetupPojo.getOfficeCode())
                .build();

        publicHolidaySetup.setActive(update.getActive());

        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();

        try {
            beanUtilsBean.copyProperties(update, publicHolidaySetup);
        } catch (Exception e) {
            throw new RuntimeException("It does not exist");
        }

        publicHolidaySetupRepo.save(update);
    }

    @Override
    public void softDeleteHoliday(Integer holidayId) {
        publicHolidaySetupRepo.softDelete(holidayId);
    }

    @Override
    public void hardDelete(Integer holidayId) {
        publicHolidaySetupRepo.deletePublicHoliday(holidayId);
    }

    @Override
    public ArrayList<HolidayMapperPojo> getHolidayFor(String holidayFor) {
        return publicHolidayMapper.getHolidayFor(holidayFor);
    }

}
