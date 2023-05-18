package com.gerp.usermgmt.util;

import com.gerp.usermgmt.repo.auth.ModuleApiMappingRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ModuleApiDataUtils {


    @Autowired
    private ModuleApiMappingRepo moduleApiMappingRepo;
}
