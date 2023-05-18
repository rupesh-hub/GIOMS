package com.gerp.usermgmt.pojo.forgetPassword;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetStatusAndTokenPojo {
    private String token;
    private Boolean isExist;
    private String emailOrUsername;
}