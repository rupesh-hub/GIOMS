package com.gerp.usermgmt.pojo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleApiMappingRequestPojo {

    private List<ModuleApiMappingPojo> moduleApiMapping;

    private Boolean syncFileMapping;
}
