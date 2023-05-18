package com.gerp.usermgmt.pojo;

import com.gerp.shared.utils.FieldErrorConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModulePrivilageGroup {
    @NotNull
    private Long moduleId;
    @NotNull
    private Long privilegeId;
}
