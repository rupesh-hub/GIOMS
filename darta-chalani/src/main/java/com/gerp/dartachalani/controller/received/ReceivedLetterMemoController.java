package com.gerp.dartachalani.controller.received;

import com.gerp.dartachalani.constant.PermissionConstants;
import com.gerp.dartachalani.dto.MemoResponsePojo;
import com.gerp.dartachalani.dto.ReceivedLetterMemoRequestPojo;
import com.gerp.dartachalani.model.receive.ReceivedLetterMemo;
import com.gerp.dartachalani.service.ReceivedLetterMemoService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@RestController
@RequestMapping("/received-letter-memo")
public class ReceivedLetterMemoController extends GenericCrudController<ReceivedLetterMemo, Long> {

    private final ReceivedLetterMemoService receivedLetterMemoService;
    private final CustomMessageSource customMessageSource;

    public ReceivedLetterMemoController(ReceivedLetterMemoService receivedLetterMemoService, CustomMessageSource customMessageSource) {
        this.receivedLetterMemoService = receivedLetterMemoService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.RECEIVED_LETTER_MODULE_NAME;
        this.permissionName = PermissionConstants.RECEIVED_LETTERS + "_" + PermissionConstants.RECEIVED_LETTER;
    }

    @PostMapping("/save")
    public ResponseEntity<?> create(@Valid @RequestBody ReceivedLetterMemoRequestPojo receivedLetterMemoRequestPojo, BindingResult bindingResult) throws BindException {

        if(!bindingResult.hasErrors()) {
            ReceivedLetterMemo receivedLetterMemo = receivedLetterMemoService.saveLetterMemo(receivedLetterMemoRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            receivedLetterMemo.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/{letterId}")
    public ResponseEntity<?> getByLetterId(@PathVariable("letterId") Integer letterId) {
        ArrayList<MemoResponsePojo> memoResponsePojoList = receivedLetterMemoService.getByLetterId(letterId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }
}
