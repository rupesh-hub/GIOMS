package com.gerp.kasamu.service.impl;

import com.gerp.kasamu.constant.ErrorMessages;
import com.gerp.kasamu.converter.KasamuConverter;
import com.gerp.kasamu.mapper.CommitteeIndicatorMapper;
import com.gerp.kasamu.model.committee.CommitteeIndicator;
import com.gerp.kasamu.model.kasamu.KasamuMaster;
import com.gerp.kasamu.pojo.request.CommitteeIndicatorRequestPojo;
import com.gerp.kasamu.pojo.response.CommitteeIndicatorResponsePojo;
import com.gerp.kasamu.repo.CommitteeIndicatorRepository;
import com.gerp.kasamu.repo.KasamuMasterRepository;
import com.gerp.kasamu.service.CommitteeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommitteeServiceImpl implements CommitteeService {
    private final KasamuConverter kasamuConverter;
    private final CommitteeIndicatorRepository committeeIndicatorRepository;
    private final KasamuMasterRepository kasamuMasterRepository;
    private final CommitteeIndicatorMapper committeeIndicatorMapper;

    public CommitteeServiceImpl(KasamuConverter kasamuConverter, CommitteeIndicatorRepository committeeIndicatorRepository, KasamuMasterRepository kasamuMasterRepository, CommitteeIndicatorMapper committeeIndicatorMapper) {
        this.kasamuConverter = kasamuConverter;
        this.committeeIndicatorRepository = committeeIndicatorRepository;
        this.kasamuMasterRepository = kasamuMasterRepository;
        this.committeeIndicatorMapper = committeeIndicatorMapper;
    }


    @Override
    public Long addCommitteeIndicator(CommitteeIndicatorRequestPojo committeeIndicatorRequestPojo) {
        CommitteeIndicator committeeIndicator = kasamuConverter.toCommitteeIndicatorEntity(committeeIndicatorRequestPojo);
        committeeIndicator.setKasamuMaster(getKasamuMaster(committeeIndicatorRequestPojo.getKasamuMasterId()));
        committeeIndicatorRepository.save(committeeIndicator);
        return committeeIndicator.getId();
    }

    @Override
    public Long updateCommitteeIndicator(CommitteeIndicatorRequestPojo committeeIndicatorRequestPojo) {
        if (committeeIndicatorRequestPojo.getId() ==  null){
            throw new RuntimeException(ErrorMessages.ID_IS_MISSING.getMessage());
        }
        CommitteeIndicator committeeIndicator = kasamuConverter.toCommitteeIndicatorEntity(committeeIndicatorRequestPojo);
        committeeIndicator.setId(committeeIndicator.getId());
        committeeIndicatorRepository.save(committeeIndicator);
        return committeeIndicator.getId();
    }

    @Override
    public List<CommitteeIndicatorResponsePojo> getCommitteeIndicator(Long kasamuMasterId, Long committeeIndicatorId) {
        return committeeIndicatorMapper.getCommitteeIndicator(kasamuMasterId,committeeIndicatorId);
    }

    @Override
    public void deleteCommitteeIndicator(Long id) {
        committeeIndicatorRepository.deleteById(id);
    }

    private KasamuMaster getKasamuMaster(Long id) {
        Optional<KasamuMaster> kasamuMasterOptional = kasamuMasterRepository.findById(id);
        if (!kasamuMasterOptional.isPresent()){
            throw new RuntimeException(ErrorMessages.KASAMU_MASTER_NO_FOUND.getMessage());
        }
        return kasamuMasterOptional.orElse(new KasamuMaster());
    }
}
