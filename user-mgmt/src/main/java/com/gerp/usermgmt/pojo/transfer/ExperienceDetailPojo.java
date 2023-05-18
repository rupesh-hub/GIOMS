package com.gerp.usermgmt.pojo.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceDetailPojo {
    public String sn;
    public String organizationName;
    public String designation;
    public String service;
    public String category;
    public String fromDate;
    public String toDate;
    public String month;
    public String year;
    public String location;
}
