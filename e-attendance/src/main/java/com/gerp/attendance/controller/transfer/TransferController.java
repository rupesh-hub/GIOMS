package com.gerp.attendance.controller.transfer;

import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.service.*;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.ApiResponsePojo;
import com.gerp.shared.pojo.GlobalApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/transfer")
public class TransferController extends BaseController {

    private final CustomMessageSource customMessageSource;
    private final TransferService transferService;

    public TransferController(CustomMessageSource customMessageSource,TransferService transferService) {
        this.customMessageSource = customMessageSource;
        this.transferService = transferService;
        this.moduleName = PermissionConstants.TRANSFER_MODULE_NAME;
        this.permissionName = PermissionConstants.TRANSFER + "_" + PermissionConstants.TRANSFER_SETUP;
    }


    @GetMapping("/attendance-transfer")
    public ResponseEntity<?> validateEmployeeTransfer(@RequestParam(name = "pisCode") String  pisCode,@RequestParam(name = "targetOfficeCode") String  targetOfficeCode){
        GlobalApiResponse message = transferService.validatePisCodeData(pisCode,targetOfficeCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName)),
                        message)
        );
    }

    @GetMapping("/attendance-transfer-approve")
    public ResponseEntity<?> approveTransfer(@RequestParam(name = "pisCode") String  pisCode,@RequestParam(name = "targetOfficeCode") String  targetOfficeCode){
        GlobalApiResponse message = transferService.approveEmployeeTransfer(pisCode,targetOfficeCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName)),
                        message)
        );
    }
}
