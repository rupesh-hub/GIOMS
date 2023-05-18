package com.gerp.usermgmt.pojo.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MobileScreenPrivilegePojo {
    private String key;
    private List<MobileScreenPrivilegePojo> subMenus;
    private List<MobileSubModulePojo> subModules;
}
