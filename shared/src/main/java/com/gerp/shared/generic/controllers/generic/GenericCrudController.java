package com.gerp.shared.generic.controllers.generic;

import com.gerp.shared.generic.api.BaseEntity;
import com.gerp.shared.generic.api.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.io.Serializable;


public abstract class GenericCrudController<T extends BaseEntity, ID extends Serializable> extends GenericCrudBaseController<T, ID> {

    @Autowired
    private GenericService<T, ID> genericService;

//    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody T entity, BindingResult bindingResult) throws BindException {
//        DayType.getKey()
        if (!bindingResult.hasErrors()) {
            try {
                T t = genericService.create(entity);
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

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody T entity, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            genericService.update(entity);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            entity)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }
}
