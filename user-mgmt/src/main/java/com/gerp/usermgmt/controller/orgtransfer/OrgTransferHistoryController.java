package com.gerp.usermgmt.controller.orgtransfer;

import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.usermgmt.pojo.organization.orgtransfer.OrgTransferRequestPojo;
import com.gerp.usermgmt.services.orgtransfer.OrgTransferHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/org-transfer-history")
public class OrgTransferHistoryController extends BaseController {
    private final OrgTransferHistoryService orgTransferHistoryService;

    public OrgTransferHistoryController(OrgTransferHistoryService orgTransferHistoryService) {
        this.orgTransferHistoryService = orgTransferHistoryService;
        this.moduleName = "TransferHistory";
    }

    @GetMapping("/{pisCode}")
    @Operation(summary = "transfer history ", tags = {"USER MANAGEMENT"})
    @ApiResponse(responseCode = "200", description = "Returns a object", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrgTransferRequestPojo.class))})
    public ResponseEntity<?> transferDetail(@PathVariable("pisCode") String pisCode){
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(CrudMessages.get, customMessageSource.get(moduleName)),
                        orgTransferHistoryService.transferHistory(pisCode)));
    }


    /**
     * get transfer history of employee
     * @param pisCode
     * @return
     */
    @GetMapping("/employee")
    public ResponseEntity<?> getTransferHistoryList(@RequestParam(name="pisCode") String pisCode){

        List<Map<String,Object>> list = orgTransferHistoryService.getTransferHistory(pisCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName)),
                        list)
        );
    }
}
