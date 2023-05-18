package com.gerp.usermgmt.pojo.organization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressPojo {
    private Integer id;
    private Integer wardNo;
    private String streetAddress;
    private String description;
}
