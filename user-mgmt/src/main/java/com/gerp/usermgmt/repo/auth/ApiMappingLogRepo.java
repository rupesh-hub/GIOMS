package com.gerp.usermgmt.repo.auth;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.auth.ApiMappingLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiMappingLogRepo extends GenericRepository<ApiMappingLog, Long> {

    @Query(value = "select * from api_mapping_log aml where aml.is_active = true order by aml.created_date desc limit 1 ", nativeQuery = true)
    ApiMappingLog getApiMappingLogByCreatedDateAndIsActive();
}
