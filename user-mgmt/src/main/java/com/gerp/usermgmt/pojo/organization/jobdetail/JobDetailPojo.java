package com.gerp.usermgmt.pojo.organization.jobdetail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobDetailPojo {

    private String pisCode;

    List<DesignationDetailPojo> designationDetailPojos;


}
