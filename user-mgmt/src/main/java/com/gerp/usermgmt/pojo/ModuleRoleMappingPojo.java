package com.gerp.usermgmt.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleRoleMappingPojo {

    private Long id;

    @NotBlank
    @NotNull
    private String name;

    private List<PrivilegeRoleMappingPojo> privilegeList;
}
