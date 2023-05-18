package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.dispatch.DispatchLetterReceiverInternal;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface DispatchLetterReceiverInternalRepo extends GenericSoftDeleteRepository<DispatchLetterReceiverInternal, Long> {

    @Modifying
    @Query(value = "update dispatch_letter_receiver_internal e set is_active= case when e.is_active=true then false else true end where e.dispatch_letter_id = ?1", nativeQuery = true)
    void deleteByDispatchLetter(Long id);

    @Query(value = "select * from dispatch_letter_receiver_internal where receiver_pis_code=?1 and dispatch_letter_id=?2", nativeQuery = true)
    List<DispatchLetterReceiverInternal> getDispatchLetterByPisCodeAndDispatchId(String pisCode, Long dispatchId);

    @Query(value = "select * from dispatch_letter_receiver_internal where receiver_pis_code = ?1 and receiver_section_id = ?2 and dispatch_letter_id=?3 LIMIT 1", nativeQuery = true)
    DispatchLetterReceiverInternal getInternalByReceiver(String pisCode, String sectionId, Long dispatchId);

    @Query(value = "select * from dispatch_letter_receiver_internal where receiver_pis_code in ?1 and receiver_section_id = ?2 and dispatch_letter_id=?3 and is_active = true LIMIT 1", nativeQuery = true)
    DispatchLetterReceiverInternal getInternalByReceiverActive(Set<String> pisCode, String sectionId, Long dispatchId);

    @Query(value = "select * from dispatch_letter_receiver_internal where receiver_pis_code = ?1 and receiver_section_id = ?2 and dispatch_letter_id=?3 and is_active = true LIMIT 1", nativeQuery = true)
    DispatchLetterReceiverInternal getInternalByReceiverActiveOwn(String pisCode, String sectionId, Long dispatchId);
}
