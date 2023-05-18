package com.gerp.dartachalani.service.kasamu;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.dto.StatusPojo;
import com.gerp.dartachalani.dto.kasamu.*;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;

import java.util.Map;

public interface KasamuService {
    KasamuResponsePojo save(KasamuRequestPojo kasamuRequestPojo);

    KasamuResponsePojo forward(KasamuStateRequestPojo kasamuStateRequestPojo);

    KasamuResponsePojo update(KasamuRequestPojo kasamuRequestPojo, Long id);

    KasamuResponsePojo getById(Long id);

    Page<KasamuResponsePojo> getCreatedKasamuList(GetRowsRequest paginateRequest);

    Page<KasamuResponsePojo> getInboxKasamuList(GetRowsRequest paginateRequest);

    Page<KasamuResponsePojo> getFinalizedKasamuList(GetRowsRequest paginateRequest);

    Page<KasamuResponsePojo> getEmployeeList(GetRowsRequest paginateRequest);

    Boolean finalizedKasamu(StatusPojo statusPojo);

    Map<String, Object> getSearchRecommendationCreate(GetRowsRequest getRowsRequest);

    Map<String, Object> getSearchRecommendationInbox(GetRowsRequest getRowsRequest);

    Map<String, Object> getSearchRecommendationFinalized(GetRowsRequest getRowsRequest);
}
