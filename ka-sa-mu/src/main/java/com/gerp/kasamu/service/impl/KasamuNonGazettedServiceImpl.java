package com.gerp.kasamu.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.gerp.kasamu.Helper.HelperUtil;
import com.gerp.kasamu.mapper.KasamuNonGazettedMapper;
import com.gerp.kasamu.model.kasamu.KasamuForNoGazetted;
import com.gerp.kasamu.pojo.Encrypted.KasamuNonGazettedEn;
import com.gerp.kasamu.pojo.request.KasamuForNoGazettedPojo;
import com.gerp.kasamu.pojo.response.TaskResponse;
import com.gerp.kasamu.repo.KasamuMasterRepository;
import com.gerp.kasamu.repo.KasamuNonGazettedRepo;
import com.gerp.kasamu.service.KasamuNonGazettedService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.utils.CrudMessages;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KasamuNonGazettedServiceImpl implements KasamuNonGazettedService {
    private final KasamuNonGazettedRepo kasamuNonGazettedRepo;
    private final CustomMessageSource customMessageSource;
    private final KasamuNonGazettedMapper kasamuNonGazettedMapper;
    private final KasamuMasterRepository kasamuMasterRepository;
    private final Gson gson = new Gson();


    public KasamuNonGazettedServiceImpl(KasamuNonGazettedRepo kasamuNonGazettedRepo, CustomMessageSource customMessageSource, KasamuNonGazettedMapper kasamuNonGazettedMapper, KasamuMasterRepository kasamuMasterRepository) {
        this.kasamuNonGazettedRepo = kasamuNonGazettedRepo;
        this.customMessageSource = customMessageSource;
        this.kasamuNonGazettedMapper = kasamuNonGazettedMapper;
        this.kasamuMasterRepository = kasamuMasterRepository;
    }

    @SneakyThrows
    @Override
    public Long addKasamuNonGazetted(KasamuForNoGazettedPojo kasamuForNoGazettedPojo) {
        KasamuForNoGazetted kasamuForNoGazetted = new KasamuForNoGazetted();
        KasamuNonGazettedEn kasamuNonGazettedEn = new KasamuNonGazettedEn();
        BeanUtils.copyProperties(kasamuNonGazettedEn,kasamuForNoGazettedPojo);
        kasamuForNoGazetted.setKasamuNonGazettedEn(gson.toJson(kasamuNonGazettedEn));
        kasamuNonGazettedRepo.save(kasamuForNoGazetted);
        return kasamuForNoGazetted.getId();
    }

    @SneakyThrows
    @Override
    public Long updateKasamuNonGazetted(KasamuForNoGazettedPojo kasamuForNoGazettedPojo) {
        if (kasamuForNoGazettedPojo.getId() == null){
            throw new RuntimeException(customMessageSource.get(CrudMessages.parameterMissing,"ID"));
        }
        Optional<KasamuForNoGazetted> kasamuForNoGazettedOptional = kasamuNonGazettedRepo.findById(kasamuForNoGazettedPojo.getId());
        if (!kasamuForNoGazettedOptional.isPresent()){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"Kasamu"));
        }
        KasamuForNoGazetted kasamuForNoGazetted = kasamuForNoGazettedOptional.orElse(new KasamuForNoGazetted());
        KasamuNonGazettedEn kasamuNonGazettedEn = gson.fromJson(kasamuForNoGazetted.getKasamuNonGazettedEn(),KasamuNonGazettedEn.class);
       BeanUtils.copyProperties(kasamuNonGazettedEn,kasamuForNoGazettedPojo);
       kasamuForNoGazetted.setKasamuNonGazettedEn(gson.toJson(kasamuNonGazettedEn));
       kasamuNonGazettedRepo.save(kasamuForNoGazetted);
        return kasamuForNoGazettedPojo.getId();
    }

    @Override
    public TaskResponse getKasamuNonGazetted(Long id, String fiscalYear, String evaluationPeriod) {
        Long code = HelperUtil.getLoginEmployeeCode()!= null?Long.parseLong(HelperUtil.getLoginEmployeeCode()):null;
        TaskResponse taskResponse = new TaskResponse();
        List<KasamuForNoGazettedPojo> kasamuForNoGazettedPojos = kasamuNonGazettedMapper.getNonGazettedList(id,code);
        kasamuForNoGazettedPojos.parallelStream().forEach(obj->{
            try {
                KasamuNonGazettedEn kasamuNonGazettedEn = gson.fromJson(obj.getKasamuNonGazettedEn(),KasamuNonGazettedEn.class);
                BeanUtils.copyProperties(obj,kasamuNonGazettedEn);
                obj.setKasamuNonGazettedEn(null);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        taskResponse.setKasamuForNoGazettedPojos(kasamuForNoGazettedPojos);
        taskResponse.setIsFiscalYearSubmitted(kasamuMasterRepository.existsByFiscalYearAndEmployeePisCodeAndValuationPeriod(fiscalYear,HelperUtil.getLoginEmployeeCode(),evaluationPeriod));
        return taskResponse;
    }

    @Override
    public Long removeKasamuNonGazetted(Long id) {
        kasamuNonGazettedRepo.deleteById(id);
        return id;
    }
}
