package com.gerp.usermgmt.pojo.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SaruwaRequestPojo {
    public String date;
    public EmployeeOtherDetailsPojo employeeDetail;
    public DesignationDetailPojo designationDetail;
    public List<SaruwaPriority> saruwaPriority;
    public List<ExperienceDetailPojo> experienceDetail;
    public ChecklistTemplatePojo saruwaRequestDetail;
}
