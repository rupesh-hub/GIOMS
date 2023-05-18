package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.dispatch.DispatchLetterReceiverExternal;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DispatchLetterReceiverExternalRepo extends GenericSoftDeleteRepository<DispatchLetterReceiverExternal, Long> {

    @Modifying
    @Query(value = "update dispatch_letter_receiver_external e set is_active= case when e.is_active=true then false else true end where e.dispatch_letter_id = ?1", nativeQuery = true)
    void deleteByDispatchLetter(Long id);
}
