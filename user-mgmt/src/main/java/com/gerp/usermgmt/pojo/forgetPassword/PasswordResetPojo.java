package com.gerp.usermgmt.pojo.forgetPassword;

import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordResetPojo {

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String emailOrUsername;
    @Size(max = StringConstants.DEFAULT_MAX_LIMIT)
    private String newPassword;
    @Size(max = StringConstants.DEFAULT_MAX_LIMIT)
    private String token;
}