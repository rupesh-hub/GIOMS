package com.gerp.usermgmt.pojo.auth;

import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordPojo {

    @NotNull
    @NotBlank
    @Size(min = StringConstants.DEFAULT_MIN_SIZE)
    private String oldPassword;
    @NotNull
    @NotBlank
    @Size(min = StringConstants.DEFAULT_MIN_SIZE)
    private String newPassword;
}
