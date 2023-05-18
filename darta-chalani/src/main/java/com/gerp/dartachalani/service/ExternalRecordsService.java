package com.gerp.dartachalani.service;

import com.gerp.dartachalani.dto.ExternalRecordsDto;
import com.gerp.dartachalani.dto.ExternalRequestPojo;
import com.gerp.dartachalani.dto.UserDetailsPojo;
import com.gerp.dartachalani.model.external.ExternalRecords;
import com.gerp.shared.generic.api.GenericService;

public interface ExternalRecordsService extends GenericService<ExternalRecords, Long> {
    ExternalRecords save(ExternalRequestPojo data);

    ExternalRecordsDto getById(UserDetailsPojo data);
}
