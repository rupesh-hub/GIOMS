package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.dispatch.DispatchLetter;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import net.sf.ehcache.concurrent.LockType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Optional;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

public interface DispatchLetterRepo extends GenericSoftDeleteRepository<DispatchLetter, Long> {

    @Modifying
    @Query(value = "UPDATE dispatch_letter SET is_active = false WHERE id = ?1", nativeQuery = true)
    void softDelete(Long letterId);

    @Modifying
    @Query(value = "UPDATE dispatch_letter SET is_archive = true WHERE id = ?1", nativeQuery = true)
    void archive(Long letterId);

    @Modifying
    @Query(value = "UPDATE dispatch_letter SET is_archive = false WHERE id = ?1", nativeQuery = true)
    void restore(Long letterId);

    @Modifying
    @Query(value = "UPDATE dispatch_letter SET is_draft = false WHERE id = ?1", nativeQuery = true)
    void setIsDraft(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DispatchLetter> findDispatchLetterById(@Param("id") Long id);

    @Modifying
    @Query(value = "update dispatch_letter set sender_pis_code = ?1, sender_section_code = ?2, sender_designation_code = ?3, is_draft = false where id = ?4", nativeQuery = true)
    void updateDispatchLetterForDraftShare(String pisCode, String sectionCode, String designationCode, Long memoId);

}
