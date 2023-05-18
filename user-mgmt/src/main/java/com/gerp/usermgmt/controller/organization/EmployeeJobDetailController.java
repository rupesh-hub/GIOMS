package com.gerp.usermgmt.controller.organization;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.employee.EmployeeJobDetailLog;
import com.gerp.usermgmt.pojo.organization.employee.EmployeePromotionPojo;
import com.gerp.usermgmt.pojo.organization.jobdetail.JobDetailPojo;
import com.gerp.usermgmt.pojo.organization.office.OfficePojo;
import com.gerp.usermgmt.services.organization.jobdetail.EmployeeJobDetailService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee-job-detail")
public class EmployeeJobDetailController extends GenericCrudController<EmployeeJobDetailLog, Long> {
    private  final EmployeeJobDetailService employeeJobDetailService;

    public EmployeeJobDetailController(EmployeeJobDetailService employeeJobDetailService) {
        this.employeeJobDetailService = employeeJobDetailService;
        this.moduleName = PermissionConstants.EMPLOYEE;

    }

    @Override
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EmployeeJobDetailLog entity, BindingResult bindingResult) throws BindException {
//        DayType.getKey()
        if (!bindingResult.hasErrors()) {
            try {
                EmployeeJobDetailLog t = employeeJobDetailService.create(entity);
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                                t.getId())
                );
            }catch (Exception e){
                return ResponseEntity.ok(
                        errorResponse(customMessageSource.get("unique.value.constraint"), customMessageSource.get(moduleName.toLowerCase()))
                );
            }
        } else {

            throw new BindException(bindingResult);

        }
    }

    @Override
    @PostMapping(value = "list")
    public ResponseEntity<?> getPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        Page<EmployeePromotionPojo> page = employeeJobDetailService.getPaginatedFilteredData(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    /**
     * employee designation history list
     * @param pisCode
     * @param officeCode
     * @return
     */
    @GetMapping(value = "/history")
    public ResponseEntity<?> getDesignationHistory(@RequestParam("pisCode") String pisCode,@RequestParam("officeCode") String officeCode){
        List<Map<String,Object>> designationList= employeeJobDetailService.getDesignationHistory(pisCode,officeCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        designationList)
        );
    }

    @GetMapping(value = "/office-history")
    public ResponseEntity<?> getAllDesignationHistoryByPisCode(@RequestParam("pisCode") String pisCode){

        JobDetailPojo  jobDetailPojo = employeeJobDetailService.getDesignationHistoryByPisCode(pisCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        jobDetailPojo)
        );
    }



}
