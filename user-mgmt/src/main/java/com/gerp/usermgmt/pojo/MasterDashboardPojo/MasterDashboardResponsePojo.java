package com.gerp.usermgmt.pojo.MasterDashboardPojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MasterDashboardResponsePojo {
    private TopTenOfficeDetailPojo topTenOfficeDetailList;
    private List<MasterDetailPojo> officeDetails;
}
