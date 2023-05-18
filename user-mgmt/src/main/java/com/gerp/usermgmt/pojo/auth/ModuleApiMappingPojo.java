package com.gerp.usermgmt.pojo.auth;

import com.gerp.shared.enums.ApiMethod;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ModuleApiMappingPojo {


    private Long id;

    private Long moduleId;
    private String moduleKey;

    private Long privilegeId;
    private String privilegeKey;

    private String api;

    private ApiMethod method;

    private Boolean isActive;

}
