package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.DelegationTableMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DelegationTableMapperRepo extends JpaRepository<DelegationTableMapper, Long> {

    @Query(value = "select * from delegation_table_mapper where dispatch_id = ?1 LIMIT 1", nativeQuery = true)
    DelegationTableMapper getByDispatchId(Long dispatchLetterId);

}
