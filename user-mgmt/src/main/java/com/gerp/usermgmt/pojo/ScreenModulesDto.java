package com.gerp.usermgmt.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenModulesDto {
    private Long screenId;
    private String screenName;
    private List<ModuleDto> moduleDtoList;

}
