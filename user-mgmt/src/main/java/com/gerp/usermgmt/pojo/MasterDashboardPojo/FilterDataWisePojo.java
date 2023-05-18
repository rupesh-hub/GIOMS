package com.gerp.usermgmt.pojo.MasterDashboardPojo;

import com.gerp.shared.enums.DashboardParamStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterDataWisePojo {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date fromDatEn;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date toDateEn;
    private String fromDateNp;
    private String toDateNp;

    private Integer limit;
    private Integer pageNo;

    private Integer orderBy;
    private Integer orderType;

}
