package com.gerp.shared.pojo.json;

import com.gerp.shared.enums.ApiMethod;
import com.gerp.shared.enums.ApiPrivilege;
import lombok.Data;

@Data
public class ApiDetail {
    private String name;
    private ApiMethod method;
    private ApiPrivilege privilege;
    private String description;
}
