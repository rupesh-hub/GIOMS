package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.Years;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.FiscalYearPojo;
import com.gerp.usermgmt.constant.PermissionConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/years")
public class YearController extends BaseController {
    private final CustomMessageSource customMessageSource;

    public YearController(CustomMessageSource customMessageSource) {
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.FISCAL_YEAR;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getFiscalYearsDetails() {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        Years.years)
        );

    }

}
