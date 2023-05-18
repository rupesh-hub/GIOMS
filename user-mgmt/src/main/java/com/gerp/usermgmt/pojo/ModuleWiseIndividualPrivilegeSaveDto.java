package com.gerp.usermgmt.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleWiseIndividualPrivilegeSaveDto {
    private Long mappingId;
    private Long roleGroupId;
    private Long screenId;
    private Long moduleId;
    private Long privilegeId;
    private Boolean checked;
}
