package com.gerp.shared.generic.controllers.generic;

import com.gerp.shared.generic.api.BaseEntity;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;


public abstract class GenericCrudBaseController<T extends BaseEntity, ID extends Serializable> extends BaseController {

    @Autowired
    private GenericService<T, ID> genericService;

    //
//@PreAuthorize("hasPermission(#this.this.permissionName,'read')")
    @GetMapping
    public ResponseEntity<?> list() {
        List<T> list = genericService.getAll();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        list)
        );
    }

    //
//@PreAuthorize("hasPermission(#this.this.permissionName,'read')")
    @GetMapping(value = "with-inactive")
    public ResponseEntity<?> listWithInActive() {
        List<T> list = genericService.findAllWithInactive();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", customMessageSource.get(moduleName.toLowerCase())),
                        list)
        );
    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> delete(@PathVariable ID id) throws Exception {
        T t = genericService.findById(id);
        if (t != null) {
            genericService.deleteById(id);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        } else {
            return ResponseEntity.ok(
                    errorResponse(customMessageSource.get("crud.not_exits", customMessageSource.get(moduleName.toLowerCase())),
                            null)
            );
        }
    }

    //
//@PreAuthorize("hasPermission(#this.this.permissionName,'read')")
    @GetMapping(value = "{id}")
    public ResponseEntity<?> get(@PathVariable ID id) {
        T t = genericService.findById(id);
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

    //
//@PreAuthorize("hasPermission(#this.this.permissionName,'read')")
    @PostMapping(value = "paginated")
    public ResponseEntity<?> getPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        Page<T> page = genericService.getPaginated(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

//

//	@PostMapping(value = "/paginated-with-inactive")
//	public ResponseEntity<?> getPaginatedWithInactive(@RequestBody GetRowsRequest paginatedRequest) {
//		Page<T> page = genericService.getPaginatedInactive(paginatedRequest);
//		return ResponseEntity.ok(
//				successResponse(customMessageSource.get("crud.get",moduleName),
//						page)
//		);
//	}
}
