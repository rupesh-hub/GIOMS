package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.dispatch.SignatureData;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import org.springframework.data.jpa.repository.Query;

public interface SignatureDataRepo extends GenericSoftDeleteRepository<SignatureData, Long> {

    @Query(value = "select * from signature_data where dispatch_letter_id = ?1", nativeQuery = true)
    SignatureData getByDispatchId(Long id);

}
