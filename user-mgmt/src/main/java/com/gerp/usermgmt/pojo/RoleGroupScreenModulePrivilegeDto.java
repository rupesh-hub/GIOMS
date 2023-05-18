package com.gerp.usermgmt.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class RoleGroupScreenModulePrivilegeDto {
    private Long roleGroupId;
    private Long screenId;
    private Long moduleId;
    private Long[] privilegeList;
}
