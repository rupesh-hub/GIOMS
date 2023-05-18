package com.gerp.usermgmt.services.MasterDashboard;

import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.FilterDataWisePojo;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.MasterDashboardPojo;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.MasterDashboardResponsePojo;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.OfficeWiseDetailPojo;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;

public interface MasterDashboardService {
    MasterDashboardResponsePojo getMasterDashboardData(OfficeWiseDetailPojo officeWiseDetailPojo);

    MasterDashboardResponsePojo getTopOfficeDetail(FilterDataWisePojo filterDataWisePojo);

    InputStreamSource getTopOfficeDetailExcel(FilterDataWisePojo filterDataWisePojo, String lan);

//    byte[] generateReport(GetRowsRequest paginatedRequest,Integer reportType);


}
