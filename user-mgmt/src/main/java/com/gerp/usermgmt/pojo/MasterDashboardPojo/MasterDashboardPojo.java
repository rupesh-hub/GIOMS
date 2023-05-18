package com.gerp.usermgmt.pojo.MasterDashboardPojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MasterDashboardPojo {
    private FilterDataWisePojo topTenOffice;
    private OfficeWiseDetailPojo officeWiseDetail;
}
