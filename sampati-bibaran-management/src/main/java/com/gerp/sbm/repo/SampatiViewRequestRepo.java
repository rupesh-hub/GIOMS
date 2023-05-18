package com.gerp.sbm.repo;

import com.gerp.sbm.model.sampati.SampatiViewRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SampatiViewRequestRepo extends JpaRepository<SampatiViewRequest,Long> {
    @Query(nativeQuery = true,value = "select * from sampati_view_request where request_to_piscode=?1 and expired=false ")
    List<SampatiViewRequest> findRequest(String piscode);

    @Query(nativeQuery = true,value = "select * from sampati_view_request where request_to_piscode=?1 and requester_piscode=?2 and expired=false")
    SampatiViewRequest findMyReviewRequest(String pisCode,String requester_piscode);
    @Query(nativeQuery = true,value = "select * from sampati_view_request where request_to_piscode=?1 and approved=true")
    Optional<SampatiViewRequest> findStatus(String piscode);
}
