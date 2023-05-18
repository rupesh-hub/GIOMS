package com.gerp.usermgmt.controller.organization;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.pojo.employee.EmployeeSectionPojo;
import com.gerp.usermgmt.Proxy.MessagingServiceData;
import com.gerp.usermgmt.constant.IndividualScreenConstants;
import com.gerp.usermgmt.constant.ModuleConstants;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.pojo.organization.SearchPojo;
import com.gerp.usermgmt.pojo.organization.employee.*;
import com.gerp.usermgmt.pojo.transfer.FileConverterPojo;
import com.gerp.usermgmt.services.organization.employee.EmployeeService;
import com.gerp.usermgmt.services.organization.utils.ContactReportGenerator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(EmployeeController.API)
public class EmployeeController extends BaseController {

    protected static final String API = "/employee";
    private  final EmployeeService employeeService;
    private  final ContactReportGenerator contactReportGenerator;

    private final MessagingServiceData templateServiceData;


    public EmployeeController(EmployeeService employeeService, ContactReportGenerator contactReportGenerator, CustomMessageSource customMessageSource,  MessagingServiceData templateServiceData) {
        this.employeeService = employeeService;
        this.contactReportGenerator = contactReportGenerator;
        this.customMessageSource = customMessageSource;
        this.templateServiceData = templateServiceData;
        this.moduleName = PermissionConstants.EMPLOYEE;
        this.permissionName = IndividualScreenConstants.MY_OFFICE_EMPLOYEE_SCREEN + "_" + ModuleConstants.MY_OFFICE_EMPLOYEE_MODULE;
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@Valid @RequestBody KararEmployeePojo kararEmployeePojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            String pisCode= employeeService.saveEmployee(kararEmployeePojo).getPisCode();
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            pisCode)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody KararEmployeePojo employee, BindingResult bindingResult) throws BindException, InvocationTargetException, IllegalAccessException {

        if (!bindingResult.hasErrors()) {
            String pisCode= employeeService.updateKararEmployee(employee).getPisCode();
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            pisCode)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/edit/{code}")
    public ResponseEntity<?> editDetail(@PathVariable("code") String pisCode) {
            KararEmployeeChildPojo employee = employeeService.employeeAllDetail(pisCode);
            if(employee == null){
                throw new RuntimeException("employee not found");
            }
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            employee)
            );
    }

    @GetMapping("/employee-by-code")
    public ResponseEntity<?> getUpperEmployeeList() {
        List<EmployeeMinimalPojo> employees = employeeService.getAllEmployeesWithHigherOrder();
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found in higher hierarchy"));
        }
    }

    @GetMapping("/office-higher-employees-without-role")
    public ResponseEntity<?> getOfficeEmployeeHierarchy() {
        List<EmployeeMinimalPojo> employees = employeeService.getAllEmployeesWithHigherOrderWithoutRole();
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found under provided office"));
        }
    }


    @GetMapping("/approval-employees-eattandance")
    public ResponseEntity<?> getHigherApprovalEmployees() {
        List<EmployeeMinimalPojo> employees = employeeService.getAllEmployeesWithHigherOrderForEAttendance();
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found in higher hierarchy"));
        }
    }

    @GetMapping("/lower-hierarchy")
    public ResponseEntity<?> getLowerEmployeeList() {
        List<EmployeePojo> employees = employeeService.getAllEmployeesWithLowerOrder();
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found in higher hierarchy"));
        }
    }

    @GetMapping("/section-lower-hierarchy-employee")
    public ResponseEntity<?> getSectionLowerEmployeeList(@RequestParam("sectionId") Long sectionId) {
        List<EmployeeMinimalPojo> employees = employeeService.sectionLowerEmployees(sectionId);
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found in higher hierarchy"));
        }
    }

    @GetMapping("/lower-hierarchy-ids")
    public ResponseEntity<?> getLowerEmployeeListMinimal() {
        List<EmployeeMinimalPojo> employees = employeeService.getAllEmployeesWithLowerOrderMinimal();
        List<String> pisCodes = employees.stream().map(EmployeeMinimalPojo::getPisCode).collect(Collectors.toList());
        if( pisCodes.size() > 0) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            pisCodes)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found in lower hierarchy"));
        }
    }

    @GetMapping("/employee-detail")
    public ResponseEntity<?> getEmployeeDetail(@RequestParam String pisCode) {
        EmployeePojo employees = employeeService.employeeDetail(pisCode);
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get(moduleName.toLowerCase())));
        }
    }

    @GetMapping
    public ResponseEntity<?> loggedInEmployeeDetail() {
        EmployeePojo employees = employeeService.employeeDetail();
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get(moduleName.toLowerCase())));
        }
    }

    @GetMapping("/lower-hierarchy-users")
    public ResponseEntity<?> getLowerEmployeeUserList() {
        List<EmployeePojo> employees = employeeService.getAllEmployeesUserWithLowerOrder();
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found in higher hierarchy"));
        }
    }


    @GetMapping("/employee-detail-minimal/{pisCode}")
    public ResponseEntity<?> getEmployeeDetailMinimal(@PathVariable String pisCode) {
        EmployeeMinimalPojo employee = employeeService.employeeDetailMinimal(pisCode);
        if( employee != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employee)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get(moduleName.toLowerCase())));
        }
    }

    @GetMapping("/office-employee-list")
    public ResponseEntity<?> getEmployeeListOfLoggedInOffice() {
        List<EmployeePojo> employees = employeeService.employeeListOfOffice();
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found under provided office"));
        }
    }

    @GetMapping("/office-distinct-employee-list")
    public ResponseEntity<?> getUniqueEmployeeListOfLoggedInOffice() {
        List<EmployeePojo> employees = employeeService.distinctEmployeeListOfOffice();
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found under provided office"));
        }
    }

    @GetMapping("/employeeListByOffice")
    public ResponseEntity<?> getEmployeeListOfOffice(@RequestParam String officeCode) {
        List<EmployeePojo> employees = employeeService.employeeListOfByOffice(officeCode);
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found under provided office"));
        }
    }

    @GetMapping("/employeeAllDetailListByOffice")
    public ResponseEntity<?> getEmployeeAllDetailListOfOffice(@RequestParam String officeCode) {
        List<EmployeePojo> employees = employeeService.employeeAllDetailListOfByOffice(officeCode);
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found under provided office"));
        }
    }

    @GetMapping("/office-section-employee-list")
    public ResponseEntity<?> getOfficeSectionEmployeeList() {
        List<EmployeeSectionPojo> employees = employeeService.getOfficeSectionEmployeeList();
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found under provided office"));
        }
    }

    @GetMapping("/section-employee-list")
    public ResponseEntity<?> getEmployeeListBySection(@RequestParam Long id) {
        List<EmployeeSectionPojo> employees = employeeService.getSectionEmployeeList(id);
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found under provided office"));
        }
    }

    @GetMapping("/employee-by-section")
    public ResponseEntity<?> getEmployeeBySection(@RequestParam Long id) {
        List<EmployeePojo> employees = employeeService.getEmployeeListBySectionId(id);
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found under provided office"));
        }
    }

    @GetMapping("/office-head")
    public ResponseEntity<?> getOfficeHeadEmployee(@RequestParam String officeCode) {
        EmployeeMinimalPojo employee = employeeService.getOfficeHeadEmployee(officeCode);
        if( employee != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employee)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No Office Head found under provided office"));
        }
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @GetMapping("/add-section-employee/{sectionId}")
    public ResponseEntity<?> addEmployeeOnSection(@PathVariable("sectionId") Long sectionId, @RequestBody List<String> employeIds) {
        try {
            employeeService.saveEmployeeOnSection(employeIds, sectionId);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.save", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }catch (Exception ex){
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "Error while adding employee on section"));

        }
    }


    @PostMapping(value = "/employee-search")
    public ResponseEntity<?> getEmployeeContact(@RequestBody SearchPojo search) {
        List<EmployeePojo> employeeList =  employeeService.searchEmployees(search);
        if (employeeList != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            employeeList)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @PostMapping(value = "/current-office-contact/paginated")
    public ResponseEntity<?> getReportPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        Page<EmployeePojo> page = employeeService.employeeContact(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    @PostMapping(value = "/all-office-contact/paginated")
    public ResponseEntity<?> getReportPaginatedAllOffice(@RequestBody GetRowsRequest paginatedRequest) {
        Page<EmployeePojo> page = employeeService.employeeContactAllOffice(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    @PostMapping(value = "/contact-report")
    @Operation(summary = "generate contact report", tags = {"Contact Management"})
    public ResponseEntity<Resource> getReportPaginatedAllOffice(@RequestBody Map<String, Object> params, @RequestParam(defaultValue = "0") int reportType ) {
        FileConverterPojo fileConverterPojo = new FileConverterPojo(contactReportGenerator.generateReport(params , reportType));
        byte[] response = templateServiceData.getFileConverter(fileConverterPojo);
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);
        assert response != null;
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(response));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "contact-report_".concat(String.valueOf(Math.random())) + ".pdf")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(file);




//        if( response != null) {
//            return ResponseEntity.ok(
//                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
//                            response)
//            );
//        } else {
//            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found under provided office"));
//        }
    }

    @GetMapping("/section-higher-employees")
    public ResponseEntity<?> getHigherEmployeeListBySection(@RequestParam Long sectionId) {
        List<EmployeeMinimalPojo> employees = employeeService.getHigherSectionEmployeeList(sectionId);
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found under provided office"));
        }
    }


    @PostMapping("/update-employee")
    public ResponseEntity<?> create(@RequestBody Map<String, Object> params) {
        EmployeePojo employeePojo = objectMapper.convertValue(params, EmployeePojo.class);

            Employee employee= employeeService.updateEmployee(employeePojo);
            if(!ObjectUtils.isEmpty(employee)){
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                                employee.getId())
                );
            } else {
                throw new RuntimeException("Error while updating employee");
            }


    }

    @PostMapping(value = "/all-employee/paginated")
    public ResponseEntity<?> getEmployeesPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        Page<EmployeePojo> page = employeeService.employeesPaginated(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }



    @PutMapping("/update-order")
    public ResponseEntity<?> updateOrders(@Valid @RequestBody EmployeePojo employee, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            employeeService.updateEmployeeOrders(employee);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName)),
                            null ));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/activate-unassigned-employee")
    public ResponseEntity<?> activateUnassignedEmployee(@Valid @RequestBody EmployeePojo employee, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            if(employee.getPisCode() == null || employee.getEmployeeCode() == null){
                throw new ServiceValidationException("Invalid Request: piscode cannot be null");
            }
            employeeService.activateUnAssignedEmployee(employee);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName)),
                            null ));
        } else {
            throw new BindException(bindingResult);
        }
    }
    @PutMapping("/pis-update/{pisCode}")
    public ResponseEntity<?> update(@Valid @RequestBody EmployeePojo employee, @PathVariable String pisCode, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            employeeService.updatePisCode(employee, pisCode);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName)),
                            null ));
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/current-employee")
    public ResponseEntity<?> getDelegatedEmployeeDetail() {
        EmployeeMinimalPojo employees = employeeService.getCurrentUser();
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found in higher hierarchy"));
        }
    }

    @PostMapping("/promote-employee")
    public ResponseEntity<?> promoteEmployee(@Valid @RequestBody EmployeePromotionPojo employeePromotion) {
        employeeService.promoteEmployeeJobDetail(employeePromotion);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.update", customMessageSource.get(moduleName.toLowerCase())),
                            null));
    }

    @PostMapping("/upload-excel")
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file) {
        List<EmployeeErrorMessagePojo> e = employeeService.uploadExcel(file);
        if(e == null || e.isEmpty()){
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.update", customMessageSource.get(moduleName.toLowerCase())),
                            null));
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("error.cant.update", customMessageSource.get(moduleName.toLowerCase())),
                            e));
        }

    }

    @GetMapping("/special-employee-list")
    public ResponseEntity<?> getSpecialEmployeeList() {
        List<EmployeeMinimalPojo> employees = employeeService.  getSpecialEmployeeList();
        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", "No employee found under provided office"));
        }
    }

    /**
     *  all employee admin list created by super admin
     * @param paginatedRequest
     * @return
     */
    @PostMapping(value = "/employee-list-admin/paginated")
    public ResponseEntity<?> getEmployeesAdminPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        Page<EmployeePojo> page = employeeService.employeesAdminPaginated(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }
    @GetMapping(value = "/employee-list-admin/print")
    public ResponseEntity<?> getEmployeesAdminPaginated(@RequestParam("fromDate")String fromDate,@RequestParam("toDate")String toDate) {
        List<EmployeePojo> page = employeeService.employeesAdminPrint(fromDate,toDate);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    /**
     *  karar employee list karar ending date before 7 days
     * @param days
     * @return
     */
    @GetMapping(value = "karar-employee-list/{days}")
    public ResponseEntity<?> getKararEmployeeList(@PathVariable("days") Integer days) {
        List<EmployeeMinimalPojo> employeeList = employeeService.getKararEmployeeList(days);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        employeeList)
        );
    }


    /**
     * count karar employee list
     * @param days
     * @return
     */
    @GetMapping(value = "karar-employee-count/{days}")
    public Map<String,Object> getKararEmployeeCount(@PathVariable Integer days) {
        Map<String,Object> countList= employeeService.getKararEmployeeCount(days);
        return  countList;
    }

    @PostMapping(value = "save-profile-pic", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> saveProfilePic(EmployeeMinimalPojo profilePic) {
        Boolean isSaved = employeeService.saveProfilePic(profilePic);

        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.upload.profile"),
                        isSaved));
    }

    @PostMapping(value = "save-employee-wizard")
    public ResponseEntity<?> saveEmployee(@Valid @RequestBody KararEmployeePojo kararEmployeePojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            String pisCode = employeeService.saveEmployeeForWizard(kararEmployeePojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            pisCode)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping(value = "/get-employee-by-employee-code")
    public ResponseEntity<?> getEmployeeDetailsByEmployeeCode(@RequestParam("employeeCode") String employeeCode){
        EmployeePojo employees = employeeService.employeeDetailFromEmployeeCode(employeeCode);

        if( employees != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("success.retrieve", customMessageSource.get(moduleName.toLowerCase())),
                            employees)
            );
        } else {
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get(moduleName.toLowerCase())));
        }


    }
}
