package com.gerp.usermgmt.pojo;

import com.gerp.shared.utils.FieldErrorConstant;
import com.gerp.usermgmt.model.RoleGroupScreenModulePrivilege;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionWrapper {
    @NotNull
    private List<RoleGroupScreenModulePrivilege> permissionSaveDtoList;
}
