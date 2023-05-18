package com.gerp.usermgmt.pojo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualScreenRoleMappingPojo {
    private Long id;
    private String name;
    private boolean checked;
}
