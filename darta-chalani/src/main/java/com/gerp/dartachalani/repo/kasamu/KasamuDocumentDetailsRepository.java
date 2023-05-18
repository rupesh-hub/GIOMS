package com.gerp.dartachalani.repo.kasamu;

import com.gerp.dartachalani.model.kasamu.KasamuDocumentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KasamuDocumentDetailsRepository extends JpaRepository<KasamuDocumentDetails, Long> {

    @Query(value = "select id from kasamu_document_details where kasamu_id = ?1 order by id desc limit 1", nativeQuery = true)
    Long getByKasamuId(Long id);
}
