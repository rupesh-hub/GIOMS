package com.gerp.usermgmt.repo.designation;

import com.gerp.shared.generic.api.GenericSoftDeleteRepository;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
public interface FunctionalDesignationRepo extends GenericSoftDeleteRepository<FunctionalDesignation, String> {
    @Transactional
    @Modifying
    @Query("update FunctionalDesignation e set e.isActive= case when e.isActive=true then false else true end where e.code = ?1")
    void deActivateDesignation(String id);

    @Query("select max (code) from FunctionalDesignation")
    String getMaxDesignationCode();
}
