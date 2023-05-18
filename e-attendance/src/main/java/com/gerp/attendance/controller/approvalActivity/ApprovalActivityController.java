package com.gerp.attendance.controller.approvalActivity;

import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.controller.apiDoc.approvalActivity.ApprovalActivityAPIDoc;
import com.gerp.attendance.service.DecisionApprovalService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.TableEnum;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */

@RestController
@RequestMapping("/approval-activity-log")
public class ApprovalActivityController extends BaseController implements ApprovalActivityAPIDoc {

    private final DecisionApprovalService decisionApprovalService;
    private final CustomMessageSource customMessageSource;

    public ApprovalActivityController(CustomMessageSource customMessageSource, DecisionApprovalService decisionApprovalService) {
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.APPROVAL_ACTIVITY_MODULE_NAME;
        this.permissionName = PermissionConstants.SHIFT + "_" + PermissionConstants.SHIFT_SETUP;
        this.decisionApprovalService = decisionApprovalService;
    }

    /**
     * Approval Activity Get All API.
     * <p>
     * * This api is used to approval activity.
     * </p>
     *
     * @param type
     * @param id
     */
    @GetMapping(value = "/type/{type}/id/{id}")
    public ResponseEntity<?> get(@PathVariable TableEnum type, @PathVariable Long id) {

        //TODO: old
//        return ResponseEntity.ok(
//                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName)),
//                        decisionApprovalService.getActivityLogsById(id, type))
//        );

        //TODO: OPTIMIZED ONE
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName)),
                        decisionApprovalService.getActivityLog(id, type))
        );

    }

    @GetMapping(value = "/signature/{type}/{id}")
    public ResponseEntity<?> getSignature(@PathVariable TableEnum type, @PathVariable Long id) {

        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName)),
                        decisionApprovalService.checkingSignature(id))
        );
    }


}
