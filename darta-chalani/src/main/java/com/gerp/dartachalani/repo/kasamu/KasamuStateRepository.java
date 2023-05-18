package com.gerp.dartachalani.repo.kasamu;

import com.gerp.dartachalani.model.kasamu.KasamuState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KasamuStateRepository extends JpaRepository<KasamuState, Long> {

    @Query(value = "select * from kasamu_state where kasamu_id = ?1 and receiver_pis_code = ?2 and receiver_section_code = ?3 and is_active = true and is_cc = false limit 1", nativeQuery = true)
    KasamuState getActiveKasamu(Long id, String pisCode, String sectionCode);

    @Query(value = "select * from kasamu_state where kasamu_id = ?1 and receiver_pis_code = ?2 and receiver_section_code = ?3 and is_active = true and is_cc = true limit 1", nativeQuery = true)
    KasamuState getActiveKasamuCc(Long id, String pisCode, String sectionCode);

    @Query(value = "select * from kasamu_state where kasamu_id = ?1 and receiver_pis_code = ?2 and receiver_section_code = ?3 and is_active = true limit 1", nativeQuery = true)
    KasamuState getActiveKasamuAll(Long id, String pisCode, String sectionCode);

    @Query(value = "select * from kasamu_state where kasamu_id = ?1", nativeQuery = true)
    List<KasamuState> getActivityList(Long id);

    @Query(value = "select case when count(id)>0 then true else false end from kasamu_state where receiver_pis_code = ?1 and kasamu_id = ?2 and is_cc is false", nativeQuery = true)
    Boolean isValidateForFinalized(String pisCode, Long kasamuId);

    @Query(value = "select case when count(id)>0 then false else true end from kasamu_state where kasamu_id = ?1 and is_cc is false and status != 'FN' ", nativeQuery = true)
    Boolean isAllStateFinalized(Long kasamuId);

    @Query(value = "select * from kasamu_state where receiver_pis_code = ?1 and kasamu_id = ?2 and is_cc is false order by id desc limit 1", nativeQuery = true)
    KasamuState getKasamuState(String pisCode, Long KasamuId);
}
