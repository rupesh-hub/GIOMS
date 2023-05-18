package com.gerp.usermgmt.pojo.auth;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsersRolePojo {

    private Long userId;
    @NotNull
    private List<Long> ids;
}
