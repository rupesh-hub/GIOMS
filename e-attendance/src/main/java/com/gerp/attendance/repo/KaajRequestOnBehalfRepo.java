package com.gerp.attendance.repo;

import com.gerp.attendance.model.kaaj.KaajRequest;
import com.gerp.attendance.model.kaaj.KaajRequestOnBehalf;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface KaajRequestOnBehalfRepo extends GenericSoftDeleteRepository<KaajRequestOnBehalf, Integer> {
    @Transactional
    @Modifying
    @Query(value = "delete from kaaj_request_on_behalf where kaaj_request_id = ?1 ", nativeQuery = true)
    void deleteKaajRequestBehalf(Long kaajId);
}
