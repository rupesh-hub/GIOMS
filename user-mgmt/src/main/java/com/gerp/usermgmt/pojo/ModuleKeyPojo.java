package com.gerp.usermgmt.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ModuleKeyPojo {

    private Long id;

    private String name;

    private String key;
}
