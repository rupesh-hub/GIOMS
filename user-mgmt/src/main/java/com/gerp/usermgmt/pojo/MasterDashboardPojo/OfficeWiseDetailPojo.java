package com.gerp.usermgmt.pojo.MasterDashboardPojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfficeWiseDetailPojo {
    private String officeCode;
    private FilterDataWisePojo dateDetail;
}
