package com.gerp.usermgmt.controller.sectiondesignationlog;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.pojo.organization.orgtransfer.OrgTransferRequestPojo;
import com.gerp.usermgmt.services.organization.employee.EmployeeSectionDesignationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee-work-log")
public class SectionDesignationLogController extends BaseController {

    private final EmployeeSectionDesignationLogService esd;

    public SectionDesignationLogController(EmployeeSectionDesignationLogService esd) {
        this.esd = esd;
        this.moduleName = "EmployeeWorkLog";
    }

//    @GetMapping("/{pisCode}")
//    @Operation(summary = "get prev employee ", tags = {"USER MANAGEMENT"})
//    @ApiResponse(responseCode = "200", description = "Returns a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrgTransferRequestPojo.class))})
//    public ResponseEntity<?> prevEmployeeDetail(@PathVariable("pisCode") String pisCode , @RequestParam("sectionId") Long sectionId){
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),
//                        esd.getPrevEmployee(pisCode , sectionId)));
//    }

    @GetMapping("/{pisCode}")
    @Operation(summary = "get prev employee ", tags = {"USER MANAGEMENT"})
    @ApiResponse(responseCode = "200", description = "Returns a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrgTransferRequestPojo.class))})
    public ResponseEntity<?> getPrevEmployees(@PathVariable("pisCode") String pisCode , @RequestParam("sectionId") Long sectionId){
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),
                        esd.getAllPrevEmployee(pisCode , sectionId)));
    }

}
