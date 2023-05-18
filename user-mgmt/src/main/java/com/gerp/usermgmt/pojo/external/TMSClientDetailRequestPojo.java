package com.gerp.usermgmt.pojo.external;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TMSClientDetailRequestPojo {

    @NotNull(message = "Token cannot be null")
    private String token;
}
