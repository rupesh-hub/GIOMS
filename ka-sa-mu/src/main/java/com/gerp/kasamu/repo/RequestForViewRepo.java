package com.gerp.kasamu.repo;

import com.gerp.kasamu.model.kasamu.KasamuRequestForView;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestForViewRepo extends GenericSoftDeleteRepository<KasamuRequestForView, Long> {


    @Query("select kmv from KasamuRequestForView kmv where  kmv.expired is null or kmv.expired = false  ")
    List<KasamuRequestForView> findBExpiredOrNull(boolean b);
}
