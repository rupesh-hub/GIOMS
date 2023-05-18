package com.gerp.attendance.Pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DashboardCountPojo{
    private Integer lateArrived;
    private Integer earlyLeft;
    private KaajRequestCustomPojo kaajRequestPojo;
    private List<DetailPojo> holidays;
    private Double absentCount;
    List<LateEmployeePojo> lateCheckInList;
    List<LateEmployeePojo> earlyCheckOutList;




}
