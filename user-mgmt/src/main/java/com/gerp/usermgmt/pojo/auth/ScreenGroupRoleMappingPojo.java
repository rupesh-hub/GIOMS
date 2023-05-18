package com.gerp.usermgmt.pojo.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenGroupRoleMappingPojo {
    private Long id;
    private String name;
    @JsonProperty(value = "selectedScreenCount")
    private long count;
}
