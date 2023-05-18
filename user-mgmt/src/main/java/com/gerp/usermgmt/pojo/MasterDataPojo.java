package com.gerp.usermgmt.pojo;

import com.gerp.usermgmt.pojo.MasterDashboardPojo.MasterDashboardTotalPojo;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.MasterDetailPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MasterDataPojo {

    private List<MasterDetailPojo> masterDashboardPojoList;
    private Integer totalData;
    private Integer totalPages;
    private Integer currentPage;
    private MasterDashboardTotalPojo totalCount;
}
