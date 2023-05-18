package com.gerp.usermgmt.pojo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScreenPrivilegePojo {

    @NotNull
    private Long screenId;
    @NotNull
    private List<Long> privileges;

}
