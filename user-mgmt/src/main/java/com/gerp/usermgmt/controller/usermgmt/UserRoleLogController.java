package com.gerp.usermgmt.controller.usermgmt;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.mapper.usermgmt.UserMapper;
import com.gerp.usermgmt.mapper.usermgmt.UserRoleLogMapper;
import com.gerp.usermgmt.model.UserRoleLog;
import com.gerp.usermgmt.pojo.RoleLogDetailPojo;
import com.gerp.usermgmt.pojo.RoleLogResponsePojo;
import com.gerp.usermgmt.repo.UserRoleLogRepo;
import com.gerp.usermgmt.services.organization.fiscalyear.FiscalYearService;
import com.gerp.usermgmt.services.usermgmt.UserRoleLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/user-role-log")
@Slf4j
public class UserRoleLogController extends BaseController {

    private final UserRoleLogService userRoleLogService;

    private final FiscalYearService fiscalYearService;

    private final UserMapper userMapper;

    private final UserRoleLogRepo userRoleLogRepo;

    private final UserRoleLogMapper userRoleLogMapper;



    public UserRoleLogController(UserRoleLogService userRoleLogService, CustomMessageSource customMessageSource,
                                 FiscalYearService fiscalYearService,
                                 UserMapper userMapper,
                                 UserRoleLogRepo userRoleLogRepo,
                                 UserRoleLogMapper userRoleLogMapper1) {
        this.customMessageSource = customMessageSource;
        this.userRoleLogService = userRoleLogService;
        this.fiscalYearService = fiscalYearService;
        this.userMapper = userMapper;
        this.userRoleLogRepo = userRoleLogRepo;
        this.userRoleLogMapper = userRoleLogMapper1;
    }



    @GetMapping("/{pisCode}")
    public ResponseEntity<?> getRoleLog(@PathVariable("pisCode") String pisCode,@RequestParam(name = "fiscalYearCode",required = false) String fiscalYearCode) throws JsonProcessingException {
        log.info("--calling /log in UseRoleLogController---");
        log.info("--entering getRoleLog() function---");
        if(fiscalYearCode == null) {
            fiscalYearCode = fiscalYearService.getActiveYear().getCode();
        }

        List<RoleLogResponsePojo> roleLogResponsePojos = userRoleLogService.getRoleHistory(pisCode, fiscalYearCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(PermissionConstants.USER_MODULE_NAME.toLowerCase())),
                        roleLogResponsePojos)
        );
    }


}
