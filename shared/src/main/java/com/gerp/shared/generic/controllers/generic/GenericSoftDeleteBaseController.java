package com.gerp.shared.generic.controllers.generic;

import com.gerp.shared.generic.api.BaseEntity;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.pojo.ActiveToggle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;


public abstract class GenericSoftDeleteBaseController<T extends BaseEntity, ID extends Serializable> extends GenericCrudBaseController<T, ID> {

    @Autowired
    private GenericService<T, Long> genericService;

    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping(value = "/toggle")
    public ResponseEntity<?> toggle(@RequestBody ActiveToggle data) {
        genericService.deleteById(data.getId());
        return ResponseEntity.ok(
                successResponse(customMessageSource.get(data.isStatus() ? "crud.inactive" : "crud.active", customMessageSource.get(moduleName)),
                        null)
        );
    }
}
