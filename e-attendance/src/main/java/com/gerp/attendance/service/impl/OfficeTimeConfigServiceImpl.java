package com.gerp.attendance.service.impl;

import com.gerp.attendance.Pojo.shift.OfficeTimePojo;
import com.gerp.attendance.Proxy.UserMgmtServiceData;
import com.gerp.attendance.mapper.OfficeTimeConfigurationMapper;
import com.gerp.attendance.model.shift.OfficeTimeConfig;
import com.gerp.attendance.repo.OfficeTimeConfigRepo;
import com.gerp.attendance.service.OfficeTimeConfigService;
import com.gerp.attendance.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.utils.NullAwareBeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class OfficeTimeConfigServiceImpl extends GenericServiceImpl<OfficeTimeConfig, Integer> implements OfficeTimeConfigService {
    private final OfficeTimeConfigRepo officeTimeConfigRepo;
    private final OfficeTimeConfigurationMapper officeTimeConfigurationMapper;
    private final CustomMessageSource customMessageSource;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private UserMgmtServiceData userMgmtServiceData;

    public OfficeTimeConfigServiceImpl(OfficeTimeConfigRepo officeTimeConfigRepo, OfficeTimeConfigurationMapper officeTimeConfigurationMapper, CustomMessageSource customMessageSource) {
        super(officeTimeConfigRepo);
        this.officeTimeConfigRepo = officeTimeConfigRepo;
        this.customMessageSource = customMessageSource;
        this.officeTimeConfigurationMapper = officeTimeConfigurationMapper;
    }

    @Override
    public OfficeTimeConfig findById(Integer id) {
        OfficeTimeConfig officeTimeConfig = super.findById(id);
        if (officeTimeConfig == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("office.time.config")));
        return officeTimeConfig;
    }

    @Override
    public OfficeTimeConfig save(OfficeTimePojo officeTimePojo) {
        OfficeTimeConfig officeTimeConfig = new OfficeTimeConfig().builder()
                .officeCode(officeTimePojo.getOfficeCode())
                .maximumEarlyCheckout(officeTimePojo.getMaximumEarlyCheckout() == null ? null : officeTimePojo.getMaximumEarlyCheckout())
                .maximumLateCheckin(officeTimePojo.getMaximumLateCheckin() == null ? null : officeTimePojo.getMaximumLateCheckin())
                .allowedLimit(officeTimePojo.getAllowedLimit() == 0 ? 0 : officeTimePojo.getAllowedLimit())
                .build();
        return officeTimeConfigRepo.save(officeTimeConfig);
    }

    @Override
    public OfficeTimeConfig update(OfficeTimePojo officeTimePojo) {
        OfficeTimeConfig officeTimeConfig = this.findById(officeTimePojo.getId());
        OfficeTimeConfig update = new OfficeTimeConfig().builder()
                .officeCode(officeTimePojo.getOfficeCode())
                .maximumEarlyCheckout(officeTimePojo.getMaximumEarlyCheckout() == null ? null : officeTimePojo.getMaximumEarlyCheckout())
                .maximumLateCheckin(officeTimePojo.getMaximumLateCheckin() == null ? null : officeTimePojo.getMaximumLateCheckin())
                .allowedLimit(officeTimePojo.getAllowedLimit() == 0 ? 0 : officeTimePojo.getAllowedLimit())
                .build();
        BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            beanUtilsBean.copyProperties(officeTimeConfig, update);
        } catch (Exception e) {
            throw new RuntimeException("id doesnot exists");
        }
        return officeTimeConfig;

    }

    @Override
    public OfficeTimePojo getOfficeTimeByCode(String officeCode) {
        return officeTimeConfigurationMapper.getOfficeTimeByCode(officeCode);

    }

    @Override
    public List<OfficeTimePojo> getAllOfficeTime() {
        List<String> parentOfficeCodeWithSelf = userMgmtServiceData.getParentOfficeCodeWithSelf(tokenProcessorService.getOfficeCode());
        return officeTimeConfigurationMapper.getAllOfficeTime(parentOfficeCodeWithSelf);
    }
}
