package com.gerp.usermgmt.controller.organization;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.usermgmt.services.organization.office.OfficeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/local-body")
public class LocalBodyController {
    private final CustomMessageSource customMessageSource;
    private final OfficeService officeService;

    public LocalBodyController(CustomMessageSource customMessageSource, OfficeService officeService) {
        this.customMessageSource = customMessageSource;
        this.officeService = officeService;
    }
}
