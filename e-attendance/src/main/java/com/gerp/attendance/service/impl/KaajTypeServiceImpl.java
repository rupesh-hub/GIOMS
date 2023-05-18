package com.gerp.attendance.service.impl;

import com.gerp.attendance.model.kaaj.KaajType;
import com.gerp.attendance.repo.KaajTypeRepo;
import com.gerp.attendance.service.KaajTypeService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class KaajTypeServiceImpl extends GenericServiceImpl<KaajType, Integer> implements KaajTypeService {

    private final KaajTypeRepo kaajTypeRepo;
    private final CustomMessageSource customMessageSource;


    public KaajTypeServiceImpl(KaajTypeRepo kaajTypeRepo, CustomMessageSource customMessageSource) {
        super(kaajTypeRepo);
        this.kaajTypeRepo = kaajTypeRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public KaajType findById(Integer uuid) {
        KaajType kaajType = super.findById(uuid);
        if (kaajType == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("kaaj.type")));
        return kaajType;
    }




}
