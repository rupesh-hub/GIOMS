package com.gerp.usermgmt.pojo.auth;

import com.gerp.shared.utils.FieldErrorConstant;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleUpdatePojo {

    private Long id;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_DECS_SIZE)
    private String description;

    private Collection<ScreenPrivilegePojo> screens;
}
