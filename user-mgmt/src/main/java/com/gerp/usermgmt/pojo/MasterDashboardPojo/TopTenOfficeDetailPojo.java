package com.gerp.usermgmt.pojo.MasterDashboardPojo;

import com.gerp.usermgmt.pojo.MasterDataPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopTenOfficeDetailPojo {
    private MasterDataPojo kaajList;
    private MasterDataPojo leaveList;
    private MasterDataPojo tippaniList;
    private MasterDataPojo dartaList;
    private MasterDataPojo chalaniList;

}
