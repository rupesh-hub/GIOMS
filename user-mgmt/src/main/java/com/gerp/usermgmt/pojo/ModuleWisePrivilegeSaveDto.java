package com.gerp.usermgmt.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleWisePrivilegeSaveDto {
    private Long mappingId;
    private Long roleGroupId;
    private String roleGroupName;
    private Long screenId;
    private List<RoleGroupScreenModulePrivilegeDto> roleGroupScreenModulePrivilegeDtoList;
}
