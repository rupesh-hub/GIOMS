package com.gerp.usermgmt.controller.delegation;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.pojo.delegation.DelegationLogPojo;
import com.gerp.usermgmt.services.delegation.DelegationLogService;
import com.gerp.usermgmt.services.delegation.DelegationService;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("delegation-log")
public class DelegationLogController extends BaseController {

    private final DelegationLogService delegationLogService;

    public DelegationLogController(DelegationLogService delegationLogService) {
        this.delegationLogService = delegationLogService;
    }

}
