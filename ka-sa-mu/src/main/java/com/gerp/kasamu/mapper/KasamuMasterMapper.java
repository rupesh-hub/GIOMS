package com.gerp.kasamu.mapper;

import com.gerp.kasamu.pojo.response.KasamuMasterResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface KasamuMasterMapper {
    List<KasamuMasterResponsePojo> getDraft(@Param("employeeCode") String employeeCode);

    KasamuMasterResponsePojo getKasamuMasterById(@Param("id") Long id);

    List<KasamuMasterResponsePojo> getAllKasamu(@Param("employeeCode") String employeeCode, @Param("pisCode") String pisCode);

    List<KasamuMasterResponsePojo> getKasamuToBeReviewedBySupervisor(@Param("employeeCode") String employeePisCode);

    List<KasamuMasterResponsePojo> getAllKasamuToBeReviewByPurnarawal(@Param("employeeCode") String employeePisCode);

    List<KasamuMasterResponsePojo> getAllKasamuToBeReviewByCommittee(@Param("pisCode") String pisCode);

    List<KasamuMasterResponsePojo> getKasamuMasterByEmployeePisCodeANdFiscalYear(@Param("fiscalYear") String fiscalYear,@Param("pisCode") String pisCode);
}
