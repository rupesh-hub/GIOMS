package com.gerp.usermgmt.pojo.auth;

import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordChangePojo {

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String emailOrUsername;
    @Size(max = StringConstants.DEFAULT_MAX_LIMIT)
    private String newPassword;
    @Size(max = StringConstants.DEFAULT_MAX_LIMIT)
    private String confirmPassword;
}