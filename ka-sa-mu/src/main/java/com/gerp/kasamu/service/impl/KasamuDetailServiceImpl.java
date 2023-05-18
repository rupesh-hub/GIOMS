package com.gerp.kasamu.service.impl;

import com.gerp.kasamu.Helper.HelperUtil;
import com.gerp.kasamu.constant.ErrorMessages;
import com.gerp.kasamu.converter.KasamuConverter;
import com.gerp.kasamu.mapper.KasamuDetailMapper;
import com.gerp.kasamu.model.kasamu.KasamuDetail;
import com.gerp.kasamu.pojo.Encrypted.KasamuDetailEn;
import com.gerp.kasamu.pojo.request.KasamuDetailRequestPojo;
import com.gerp.kasamu.pojo.response.KasamuDetailResponsePojo;
import com.gerp.kasamu.pojo.response.TaskResponse;
import com.gerp.kasamu.repo.KasamuDetailRepository;
import com.gerp.kasamu.repo.KasamuMasterRepository;
import com.gerp.kasamu.service.KasamuDetailService;
import com.google.gson.Gson;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class KasamuDetailServiceImpl implements KasamuDetailService {

    private final KasamuConverter kasamuConverter;
    private final KasamuDetailRepository kasamuDetailRepository;
    private final KasamuDetailMapper kasamuDetailMapper;
    private final KasamuMasterRepository kasamuMasterRepository;
    private final Gson gson = new Gson();

    public KasamuDetailServiceImpl(KasamuConverter kasamuConverter, KasamuDetailRepository kasamuDetailRepository, KasamuDetailMapper kasamuDetailMapper, KasamuMasterRepository kasamuMasterRepository) {
        this.kasamuConverter = kasamuConverter;
        this.kasamuDetailRepository = kasamuDetailRepository;
        this.kasamuDetailMapper = kasamuDetailMapper;
        this.kasamuMasterRepository = kasamuMasterRepository;
    }

    @Override
    public Long addKasamuDetail(KasamuDetailRequestPojo kasamuDetailRequestPojo) {
        KasamuDetail kasamuDetail = kasamuConverter.toKasamuDetailEntity(kasamuDetailRequestPojo);
        kasamuDetailRepository.save(kasamuDetail);
        return kasamuDetail.getId();
    }

    @Override
    public Long updateKasamuDetails(KasamuDetailRequestPojo kasamuDetailRequestPojo) {
        if (kasamuDetailRequestPojo.getId() == null){
            throw new RuntimeException(ErrorMessages.ID_IS_MISSING.getMessage());
        }
        KasamuDetail kasamuDetail = kasamuConverter.toKasamuDetailEntity(kasamuDetailRequestPojo);
        kasamuDetail.setId(kasamuDetailRequestPojo.getId());
        kasamuDetailRepository.save(kasamuDetail);
        return kasamuDetail.getId();
    }

    @Override
    public TaskResponse getKasamuDetails(Long id, String fiscalYear, String evaluationPeriod) {
        Long code = HelperUtil.getLoginEmployeeCode()!= null?Long.parseLong(HelperUtil.getLoginEmployeeCode()):null;
        List<KasamuDetailResponsePojo> kasamuDetails = kasamuDetailMapper.getKasamuDetails(id, code);

        kasamuDetails.parallelStream().forEach(obj->{
            try {
                if (obj.getKasamuDetailEn() != null){
                    KasamuDetailEn kasamuDetailEn = gson.fromJson(obj.getKasamuDetailEn(),KasamuDetailEn.class);
                    BeanUtils.copyProperties(obj,kasamuDetailEn);
                    obj.setKasamuDetailEn(null);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        });
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setKasamuDetailResponsePojoList(kasamuDetails);
        taskResponse.setIsFiscalYearSubmitted(kasamuMasterRepository.existsByFiscalYearAndEmployeePisCodeAndValuationPeriod(fiscalYear,HelperUtil.getLoginEmployeeCode(),evaluationPeriod));
        return taskResponse ;
    }

    @Override
    public void removeKasamuDetails(Long id) {
        kasamuDetailRepository.deleteById(id);
    }
}
