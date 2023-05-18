package com.gerp.dartachalani.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.dto.DartaChalaniGenericPojo;
import com.gerp.dartachalani.dto.UserDetailsPojo;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;

import java.util.List;

public interface ReferenceService {
    Page<DartaChalaniGenericPojo> getReferences(GetRowsRequest paginatedRequest);

    List<EmployeeMinimalPojo> getInvolvedUsers(UserDetailsPojo data);
}
