package com.gerp.usermgmt.controller.organization;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.enums.ServiceType;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.generic.controllers.generic.GenericCrudBaseController;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.model.employee.Service;
import com.gerp.usermgmt.pojo.organization.employee.PositionPojo;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;
import com.gerp.usermgmt.services.organization.employee.ServiceGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/service-group")
@Slf4j
public class ServiceGroupController extends GenericCrudController<Service, String> {
    private final ServiceGroupService serviceGroupService;

    public ServiceGroupController(ServiceGroupService serviceGroupService) {
        this.serviceGroupService = serviceGroupService;
        this.moduleName = PermissionConstants.SERVICE;
    }

    @GetMapping("/sub-service/{serviceCode}")
    public ResponseEntity<?> getSubService(@PathVariable("serviceCode") String serviceCode) {
        List<ServicePojo> s = serviceGroupService.subService(serviceCode);
        if (s != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            s)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @GetMapping("/service-list")
    public ResponseEntity<?> getServices() {
        List<ServicePojo> s = serviceGroupService.serviceList();
        if (s != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            s)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @GetMapping("/services-hierarchy")
    public ResponseEntity<?> getParentServices(@RequestParam("serviceCode") String serviceCode) {
        Object s = serviceGroupService.getServiceHierarchy(serviceCode);
        if (s != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            s)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> create(@Valid @RequestBody ServicePojo servicePojo, BindingResult bindingResult) throws BindException {
//        DayType.getKey()
        if (!bindingResult.hasErrors()) {
            try {
                String t = serviceGroupService.save(servicePojo);
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                                t)
                );
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.ok(
                        errorResponse(customMessageSource.get("unique.value.constraint"), customMessageSource.get(moduleName.toLowerCase()))
                );
            }
        } else {

            throw new BindException(bindingResult);

        }
    }

    @PutMapping(value = "update")
    public ResponseEntity<?> update(@Valid @RequestBody ServicePojo servicePojo, BindingResult bindingResult) throws BindException {
//        DayType.getKey()
        if (!bindingResult.hasErrors()) {
            try {
                String t = serviceGroupService.update(servicePojo);
                return ResponseEntity.ok(
                        successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                                t)
                );
            } catch (Exception e) {
                log.error(e.getMessage());
                return ResponseEntity.ok(
                        errorResponse(customMessageSource.get("unique.value.constraint"), customMessageSource.get(moduleName.toLowerCase()))
                );
            }
        } else {

            throw new BindException(bindingResult);

        }
    }


    @GetMapping("/service-list-by-type")
    public ResponseEntity<?> getServices(@Param("serviceType") ServiceType serviceType) {
        List<ServicePojo> s = serviceGroupService.serviceListByType(serviceType);
        if (s != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            s)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<?> get(@PathVariable String id) {
        ServicePojo t = serviceGroupService.findServiceId(id);
        if (t != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            t)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }

    }

    @GetMapping(value = "/with-hierarchy/{id}")
    public ResponseEntity<?> getByIdWithHierarchy(@PathVariable String id) {
        Map<String, Object> t = serviceGroupService.findServiceIdWithHierarchy(id);
        if (t != null) {
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                            t)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }

    }

    @PostMapping(value = "paginated")
    public ResponseEntity<?> getPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        Page<ServicePojo> page = serviceGroupService.getPaginatedWithFilter(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

}
