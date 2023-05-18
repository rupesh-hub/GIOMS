package com.gerp.attendance.controller.GayalKatti;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.GayalKattiRequestPojo;
import com.gerp.attendance.Pojo.GayalKattiResponsePojo;
import com.gerp.attendance.constant.PermissionConstants;
import com.gerp.attendance.model.gayalKatti.GayalKatti;
import com.gerp.attendance.service.GayalKattiService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/gayal-katti")
public class GayalKattiController extends BaseController {

    private final GayalKattiService gayalKattiService;
    private final CustomMessageSource customMessageSource;

    public GayalKattiController(GayalKattiService gayalKattiService,
                                CustomMessageSource customMessageSource) {
        this.customMessageSource = customMessageSource;
        this.gayalKattiService = gayalKattiService;
        this.moduleName = PermissionConstants.GAYAL_KATTI_MODULE_NAME;
        this.permissionName = PermissionConstants.GAYAL_KATTI + "_" + PermissionConstants.GAYAL_KATTI_SETUP;
        this.permissionApproval = PermissionConstants.APPROVAL+"_"+PermissionConstants.GAYAL_KATTI_APPROVAL;
    }

    /**
     * This method adds gayal katti
     * @param gayalKattiRequestPojo
     * @param bindingResult
     * @return
     * @throws BindException
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @ModelAttribute GayalKattiRequestPojo gayalKattiRequestPojo, BindingResult bindingResult) throws BindException, ParseException {
        if (!bindingResult.hasErrors()) {
            GayalKatti gayalKatti = gayalKattiService.save(gayalKattiRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            gayalKatti.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * This method update gayal katti
     * @param gayalKattiRequestPojo
     * @param bindingResult
     * @return
     * @throws BindException
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    public ResponseEntity<?> update(@Valid @ModelAttribute GayalKattiRequestPojo gayalKattiRequestPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            gayalKattiService.updateGayalKatti(gayalKattiRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            gayalKattiRequestPojo.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    /**
     * Gets all gayal katti
     * @return
     */
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        List<GayalKattiResponsePojo> gayalKattiResponse = gayalKattiService.getAllGayalKatti();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        gayalKattiResponse)
        );
    }

    /**
     * Gets gayal katti by id
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        GayalKattiResponsePojo gayalKattiResponse = gayalKattiService.getGayalKattiById(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        gayalKattiResponse)
        );
    }

    /**
     * Filters gayal katti by piscode
     * @return
     */
    @GetMapping("/get-by-pis-code")
    public ResponseEntity<?> getByPisCode() {
        List<GayalKattiResponsePojo> gayalKattiResponse = gayalKattiService.getByPisCode();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        gayalKattiResponse)
        );
    }

    /**
     * Filters gayal katti by officeCode
     * @return
     */
    @GetMapping("/get-by-office-code")
    public ResponseEntity<?> getByOfficeCode() {
        List<GayalKattiResponsePojo> gayalKattiResponse = gayalKattiService.getByOfficeCode();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        gayalKattiResponse)
        );
    }

    /**
     * Soft delete gayal katti
     * @param id
     * @return
     */
//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping(value="{id}")
    public ResponseEntity<?> deleteGayalKatti(@PathVariable Long id)  {
        gayalKattiService.softGayalKatti(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
                        id)
        );
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update') or hasPermission(#this.this.permissionApproval,'approve') or hasPermission(#this.this.permissionApproval,'review')")
//    @PutMapping(value = "/update-status")
//    public ResponseEntity<?> updateStatus(@Valid @RequestBody ApprovalPojo data, BindingResult bindingResult) throws BindException {
//        if (!bindingResult.hasErrors()) {
//            data.setModule(this.module);
//            data.setModuleName(this.moduleName);
//            gayalKattiService.updateStatus(data);
//            GayalKattiResponsePojo gayalKattiResponsePojo = gayalKattiService.getGayalKattiById(data.getId());
//
//            if(gayalKattiResponsePojo.getApprovalDetail().getStatus().getValueEnglish().equalsIgnoreCase("Approved")){
//                return ResponseEntity.ok(
//                        successResponse(customMessageSource.get("crud.approved", customMessageSource.get(moduleName2.toLowerCase())),
//                                null)
//                );
//            }else if(gayalKattiResponsePojo.getApprovalDetail().getStatus().getValueEnglish().equalsIgnoreCase("Rejected")){
//                return ResponseEntity.ok(
//                        successResponse(customMessageSource.get("crud.rejected", customMessageSource.get(moduleName2.toLowerCase())),
//                                null)
//                );
//            }else{
//                return ResponseEntity.ok(
//                        successResponse(customMessageSource.get("crud.forward", customMessageSource.get(moduleName2.toLowerCase())),
//                                null)
//                );
//            }
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }

    /**
     * Gets gayal katti by approver pisCode
     * @return
     */
    @GetMapping(value="/get-by-approver-pis-code")
    public ResponseEntity<?> getByApproverPisCode()  {
        List<GayalKattiResponsePojo> gayalKattiResponsePojos= gayalKattiService.getGayalKattiByApprover();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        gayalKattiResponsePojos)
        );

    }

    /**
     * Paginated Data Office Head
     */
    @PostMapping(value = "/paginated")
    public ResponseEntity<?> getEmployeePaginated(@RequestBody GetRowsRequest paginatedRequest) {
        Page<GayalKattiResponsePojo> page = gayalKattiService.filterData(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

}
