package com.gerp.usermgmt.pojo.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewRetirementPlan {
    public String organizationName;
    public String organizationAddress;
    public String year;
    public String month;
    public LocalDate depositDate;
}
