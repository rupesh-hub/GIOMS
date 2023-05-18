package com.gerp.usermgmt.pojo.auth;

import com.gerp.shared.utils.FieldErrorConstant;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserUpdatePojo {
    @NotNull
    private Long id;

    private String username;

    private String email;

    private List<Long> roleIds;

    private String pisEmployeeCode;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE, min = StringConstants.DEFAULT_MIN_SIZE_USER)
    private String name;

    @NotNull
    @NotBlank
    @Pattern(message = FieldErrorConstant.PATTERN, regexp = StringConstants.NEPALI_PATTERN)
    @Size(max = StringConstants.DEFAULT_MAX_SIZE, min = StringConstants.DEFAULT_MIN_SIZE_USER)
    private String nameN;
}
