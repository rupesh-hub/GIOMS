package com.gerp.usermgmt.controller.organization;


import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.shared.pojo.FiscalYearPojo;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.fiscalyear.FiscalYear;
import com.gerp.usermgmt.services.organization.fiscalyear.FiscalYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fiscal-year")
public class FiscalYearController extends GenericCrudController<FiscalYear, String> {
    private final FiscalYearService fiscalYearService;
    private final CustomMessageSource customMessageSource;

    @Autowired
    public FiscalYearController(FiscalYearService fiscalYearService, CustomMessageSource customMessageSource) {
        this.fiscalYearService = fiscalYearService;
        this.moduleName = PermissionConstants.FISCAL_YEAR;
        this.customMessageSource = customMessageSource;
    }

    @GetMapping("/get-active-year")
    public ResponseEntity<?> getActiveYear() {
        FiscalYear fiscalYear = fiscalYearService.getActiveYear();
        if( fiscalYear != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.active", customMessageSource.get(moduleName.toLowerCase())),
                            fiscalYear)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get(moduleName.toLowerCase())));
        }
    }

    @GetMapping("/get-active-year-pojo")
    public ResponseEntity<?> getActiveYearPojo() {
            FiscalYear fiscalYear = fiscalYearService.getActiveYear();
            if( fiscalYear != null) {
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.active", customMessageSource.get(moduleName.toLowerCase())),
                                new IdNamePojo().builder()
                                        .code(fiscalYear.getCode())
                                        .name(fiscalYear.getYear())
                                        .nameN(fiscalYear.getYearNp())
                                        .build())
                );
            } else {
                throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get(moduleName.toLowerCase())));
            }
    }


    @GetMapping("/all")
    public ResponseEntity<?> getActiveYears() {
        List<IdNamePojo> fiscalYear = fiscalYearService.getALlYear();
        if( fiscalYear != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                            fiscalYear)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get(moduleName.toLowerCase())));
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getFiscalYearsDetails(@RequestParam String fiscalYear) {
        FiscalYearPojo details = fiscalYearService.getFiscalYearsDetails(fiscalYear);
        if( fiscalYear != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                            details)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get(moduleName.toLowerCase())));
        }
    }
    @GetMapping("/detail-by-code")
    public ResponseEntity<?> getFiscalYearsDetailsByCode(@RequestParam String fiscalYearCode) {
        FiscalYearPojo details = fiscalYearService.getFiscalYearsByCode(fiscalYearCode);
        if( fiscalYearCode != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                            details)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get(moduleName.toLowerCase())));
        }
    }
}
