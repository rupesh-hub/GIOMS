package com.gerp.usermgmt.pojo.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaruwaDetail {
    public String decisionDate;
    public String designation;
    public String category;
    public String office;
    public String group;
    public String service;

    public SaruwaDetail(String decisionDate, String designation, String category, String office) {
        this.decisionDate = decisionDate;
        this.designation = designation;
        this.category = category;
        this.office = office;
    }
}
