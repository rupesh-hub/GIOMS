package com.gerp.attendance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.GayalKattiRequestPojo;
import com.gerp.attendance.Pojo.GayalKattiResponsePojo;
import com.gerp.attendance.model.gayalKatti.GayalKatti;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.ApprovalPojo;

import java.text.ParseException;
import java.util.List;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

public interface GayalKattiService extends GenericService<GayalKatti, Long> {
    GayalKatti save(GayalKattiRequestPojo gayalKattiRequestPojo) throws ParseException;

    void updateGayalKatti(GayalKattiRequestPojo gayalKattiRequestPojo);

    void softGayalKatti(Long id);

    List<GayalKattiResponsePojo> getAllGayalKatti();

    GayalKattiResponsePojo getGayalKattiById(Long id);

    List<GayalKattiResponsePojo> getGayalKattiByApprover();

//    void updateStatus(ApprovalPojo data);

    List<GayalKattiResponsePojo> getByPisCode();

    List<GayalKattiResponsePojo> getByOfficeCode();

    Page<GayalKattiResponsePojo> filterData(GetRowsRequest paginatedRequest);
}
