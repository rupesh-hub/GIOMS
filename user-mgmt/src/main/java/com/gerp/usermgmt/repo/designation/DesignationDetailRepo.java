package com.gerp.usermgmt.repo.designation;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.deisgnation.DesignationDetail;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DesignationDetailRepo  extends GenericSoftDeleteRepository<DesignationDetail, Long> {

    @Query(value = "select d from DesignationDetail d where d.service.code = ?1 and d.position.code = ?2 and d.designation.code = ?3")
    List<DesignationDetail> findDesignationDetail(String serviceCode, String positionCode, String designationCode);
}
