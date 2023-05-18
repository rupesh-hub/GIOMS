package com.gerp.usermgmt.controller.organization;


import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.converter.organiztion.administrative.AdministrativeBodyConverter;
import com.gerp.usermgmt.model.administrative.AdministrativeBody;
import com.gerp.usermgmt.pojo.organization.administrative.AdministrativeBodyPojo;
import com.gerp.usermgmt.services.organization.administration.AdministrationBodyService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/administrative-body")
public class AdministrativeBodyController extends GenericCrudController<AdministrativeBody, Integer> {
    private final AdministrationBodyService administrationBodyService;
    private final AdministrativeBodyConverter administrativeBodyConverter;
    private final CustomMessageSource customMessageSource;

    public AdministrativeBodyController(AdministrationBodyService administrationBodyService,
                                        AdministrativeBodyConverter administrativeBodyConverter,
                                        CustomMessageSource customMessageSource) {
        this.administrationBodyService = administrationBodyService;
        this.administrativeBodyConverter = administrativeBodyConverter;
        this.moduleName = PermissionConstants.ADMINISTRATIVE_BODY;
        this.customMessageSource = customMessageSource;
    }

    @PostMapping(value="/save")
    public ResponseEntity<?> saveEmployee(@Valid @RequestBody AdministrativeBodyPojo administrativeBodyPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            AdministrativeBody administrativeBody = administrationBodyService.create
                    (administrativeBodyConverter.toEntity(administrativeBodyPojo));
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            administrativeBody.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody AdministrativeBodyPojo administrativeBodyPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            AdministrativeBody administrativeBody= administrationBodyService.update(administrativeBodyPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            administrativeBody.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

      @GetMapping(value="/get-by-id/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer  id)  {
        AdministrativeBodyPojo administrativeBodyPojo = administrationBodyService.getById(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        administrativeBodyPojo)
        );

    }

    @GetMapping(value="/list")
    public ResponseEntity<?> getAll()  {
        List<AdministrativeBodyPojo> administrativeBodyPojo = administrationBodyService.findAllList();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        administrativeBodyPojo)
        );

    }

    /*

    @GetMapping(value="/get-by-pisCode/{pisCode}")
    public ResponseEntity<?> getKaajRequestByPisCode(@PathVariable String  pisCode)  {
        ArrayList<KaajRequestCustomPojo> kaajRequestCustomPojos= kaajRequestService.getKaajRequestByPisCode(pisCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        kaajRequestCustomPojos)
        );

    }*/
}
