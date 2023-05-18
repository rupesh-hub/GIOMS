package com.gerp.attendance.controller.Attendance;

import com.gerp.attendance.Pojo.PostAttendanceGetPojo;
import com.gerp.attendance.Pojo.PostAttendanceRequestPojo;
import com.gerp.attendance.Pojo.PostAttendanceUpdatePojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.postAttendance.PostAttendanceRequest;
import com.gerp.attendance.model.postAttendance.PostAttendanceRequestDetail;
import com.gerp.attendance.service.PostAttendanceRequestService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.ApprovalPojo;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping("/post-attendance")
public class PostAttendanceController extends BaseController {

    private final PostAttendanceRequestService postAttendanceRequestService;
    private final CustomMessageSource customMessageSource;

    public PostAttendanceController(PostAttendanceRequestService postAttendanceRequestService, CustomMessageSource customMessageSource) {
        this.postAttendanceRequestService = postAttendanceRequestService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.POST_ATTENDANCE_MODULE_NAME;
        this.permissionName = PermissionConstants.ATTENDANCE + "_" + PermissionConstants.POST_ATTENDANCE_SETUP;
        this.permissionApproval = PermissionConstants.APPROVAL+"_"+PermissionConstants.POST_ATTENDANCE_APPROVAL;
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping("/save")
    public ResponseEntity<?> savePostAttendance(@Valid @ModelAttribute PostAttendanceRequestPojo postAttendanceRequestPojo, BindingResult bindingResult) throws BindException {

        if(!bindingResult.hasErrors()) {
            PostAttendanceRequest postAttendanceRequest = postAttendanceRequestService.savePostAttendance(postAttendanceRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            postAttendanceRequest.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
//    @PostMapping("/save")
//    public ResponseEntity<?> savePostAttendance(@Valid @RequestBody PostAttendanceRequestPojo postAttendanceRequestPojo, BindingResult bindingResult) throws BindException {
//
//        if(!bindingResult.hasErrors()) {
//            System.out.println("checking data approver code"+postAttendanceRequestPojo.getApproverPisCode());
//           PostAttendanceRequest postAttendanceRequest = postAttendanceRequestService.savePostAttendance(postAttendanceRequestPojo);
//           return ResponseEntity.ok(
//                        successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
//                                postAttendanceRequest.getId())
//           );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping(value="/update")
    public ResponseEntity<?> update(@Valid @ModelAttribute PostAttendanceUpdatePojo postAttendanceUpdatePojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
             PostAttendanceRequestDetail postAttendanceRequestDetail = postAttendanceRequestService.update(postAttendanceUpdatePojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            postAttendanceRequestDetail.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }


    /**
     * Post Attendance APPROVAL API.
     * <p>
     *      * This api is used to update approval status.
     *      ? {@link ApprovalPojo}
     *      ? used for all case
     * </p>
     * @param data
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'update') or hasPermission(#this.this.permissionApproval,'approve') or hasPermission(#this.this.permissionApproval,'review')")
    @PutMapping(value = "/update-status")
    public ResponseEntity<?> updateStatus(@Valid @RequestBody ApprovalPojo data, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            data.setModule(this.module);
            data.setModuleName(this.moduleName);
            postAttendanceRequestService.updateStatus(data);
            PostAttendanceGetPojo postAttendanceGetPojo= postAttendanceRequestService.getPostAttendanceById(data.getId());
            if(postAttendanceGetPojo.getApprovalDetail().getStatus().getValueEnglish().equalsIgnoreCase("Approved")){
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.approved", customMessageSource.get(moduleName.toLowerCase())),
                                null)
                );
            }else if(postAttendanceGetPojo.getApprovalDetail().getStatus().getValueEnglish().equalsIgnoreCase("Rejected")){
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.rejected", customMessageSource.get(moduleName.toLowerCase())),
                                null)
                );
            }else if(postAttendanceGetPojo.getApprovalDetail().getStatus().getValueEnglish().equalsIgnoreCase("Forwarded")){
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.forward", customMessageSource.get(moduleName.toLowerCase())),
                                null)
                );
            }else if(postAttendanceGetPojo.getApprovalDetail().getStatus().getValueEnglish().equalsIgnoreCase("Canceled")){
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.cancel", customMessageSource.get(moduleName.toLowerCase())),
                                null)
                );
            }else{
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.forward", customMessageSource.get(moduleName.toLowerCase())),
                                null)
                );
            }
        } else {
            throw new BindException(bindingResult);
        }
    }


    @GetMapping(value="{id}")
    public ResponseEntity<?> getPostAttendanceById(@PathVariable Long  id)  {
        PostAttendanceGetPojo postAttendanceGetPojo= postAttendanceRequestService.getPostAttendanceById(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        postAttendanceGetPojo)
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllPostAttendance()  {
        ArrayList<PostAttendanceGetPojo> postAttendanceGetPojos= postAttendanceRequestService.getAllPostAttendance();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        postAttendanceGetPojos)
        );

    }


//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping(value="{id}")
    public ResponseEntity<?> deletePostAttendance(@PathVariable Long id)  {
        postAttendanceRequestService.deletePostAttendanceById(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
                        null)
        );
    }

    @GetMapping(value="/get-by-approver-pis-code")
    public ResponseEntity<?> getPostAttendanceByApproverPisCode()  {
        ArrayList<PostAttendanceGetPojo> postAttendanceGetPojos= postAttendanceRequestService.getPostAttendanceByApprover();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        postAttendanceGetPojos)
        );

    }
}
