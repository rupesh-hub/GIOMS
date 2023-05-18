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
public class ModuleRoleMappingWithStatusPojo {

    private Boolean isChecked;

    private List<ModuleRoleMappingPojo> moduleRoleMappingPojoList;
}
