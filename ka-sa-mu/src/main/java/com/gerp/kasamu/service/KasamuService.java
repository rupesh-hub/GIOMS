package com.gerp.kasamu.service;

import com.gerp.kasamu.pojo.request.KasamuCommitteePojoRequest;
import com.gerp.kasamu.pojo.request.KasamuEvaluatorHalfYearlyRequestPojo;
import com.gerp.kasamu.pojo.request.KasamuMasterEvaluatorRequestPojo;
import com.gerp.kasamu.pojo.request.KasamuMasterRequestPojo;
import com.gerp.kasamu.pojo.response.KasamuMasterResponsePojo;

import java.util.List;

public interface KasamuService {
    Long addKasamuMaster(KasamuMasterRequestPojo kasamuMasterRequestPojo);

    Long updateKasmuMaster(KasamuMasterRequestPojo kasamuMasterRequestPojo);

    List<KasamuMasterResponsePojo> getAllKasamu(String employeeCode);

    KasamuMasterResponsePojo getKasamuMasterById(Long id, String fiscalYear, String pisCode);

    void deleteKasamuMaster(Long id);

    List<KasamuMasterResponsePojo> getDrafts(String employeeCode);

    List<KasamuMasterResponsePojo> getAllKasamuToBeReviewBySupervisor();

    List<KasamuMasterResponsePojo> getAllKasamuToBeReviewByPurnarawal();

    Long submitReview(KasamuMasterEvaluatorRequestPojo kasamuEvaluatorRequestPojo);

    Long submitReviewByCommittee(KasamuCommitteePojoRequest committeeIndicatorRequestPojo);

    List<KasamuMasterResponsePojo> getAllKasamuToBeReviewByCommittee();

    List<KasamuMasterResponsePojo> getKasamuMasterByEmployeePisCodeAndFiscalYear(String fiscalYear, String pisCode);

    Long decisionByCommitteeMembers(Long id, Boolean decision);

    Long submitReviewHalfYearly(KasamuEvaluatorHalfYearlyRequestPojo kasamuEvaluatorRequestPojo);

    Long submitReviewByEmplpoyee(KasamuEvaluatorHalfYearlyRequestPojo kasamuEvaluatorRequestPojo);
}
