package com.gerp.usermgmt.services.organization.jobdetail;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.usermgmt.model.employee.EmployeeJobDetailLog;
import com.gerp.usermgmt.pojo.organization.employee.EmployeePromotionPojo;
import com.gerp.usermgmt.pojo.organization.jobdetail.JobDetailPojo;

import java.util.List;
import java.util.Map;

public interface EmployeeJobDetailService extends GenericService<EmployeeJobDetailLog, Long> {
    EmployeeJobDetailLog getPrevActiveLog(String pisCode);

    Page<EmployeePromotionPojo> getPaginatedFilteredData(GetRowsRequest paginatedRequest);
    // fetch child below current parent

    List<Map<String,Object>> getDesignationHistory(String pisCode,String officeCode);

    JobDetailPojo getDesignationHistoryByPisCode(String pisCode);
}
