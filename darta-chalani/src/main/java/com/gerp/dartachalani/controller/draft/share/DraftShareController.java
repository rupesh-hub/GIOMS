package com.gerp.dartachalani.controller.draft.share;

import com.gerp.dartachalani.dto.DraftShareDto;
import com.gerp.dartachalani.dto.DraftShareLogPojo;
import com.gerp.dartachalani.dto.DraftSharePojo;
import com.gerp.dartachalani.dto.enums.DcTablesEnum;
import com.gerp.dartachalani.service.draft.share.DraftShareService;
import com.gerp.shared.generic.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/draft/share")
public class DraftShareController extends BaseController {

    private final DraftShareService draftShareService;

    public DraftShareController(DraftShareService draftShareService) {
        this.draftShareService = draftShareService;
    }

    /**
     *this api is to share drafted letter
     *
     * **/
    @PostMapping
    public ResponseEntity<?> share(@RequestBody DraftShareDto draftShareDto) {
        boolean draftShare = draftShareService.share(draftShareDto);
        return draftShare ? ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("draft.share", draftShareDto.getLetterType() == DcTablesEnum.DISPATCH ? customMessageSource.get("dispatch.letter") : customMessageSource.get("memo")), Boolean.TRUE)) : ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("draft.share.failed"),
                        Boolean.FALSE
                )
        );
    }

    /**
     * this api is to update status by the employee draft receiver
     * **/
    @PutMapping
    public ResponseEntity<?> update(@RequestBody DraftShareDto draftShareDto) {
        boolean draftShare = draftShareService.update(draftShareDto);
        return draftShare ? ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("draft.share.update", draftShareDto.getLetterType() == DcTablesEnum.DISPATCH ? customMessageSource.get("dispatch.letter") : customMessageSource.get("memo")), Boolean.TRUE)) : ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("draft.share.update.failed"),
                        Boolean.FALSE
                )
        );
    }

    /**
     * this api is to get list of draft share
     * **/
    @GetMapping
    public ResponseEntity<?> getDraftList(@RequestParam(value = "dispatchId", required = false) Long dispatchId,
                                          @RequestParam(value = "memoId", required = false) Long memoId) {
        List<DraftSharePojo> list = draftShareService.getDraftShareList(dispatchId, memoId);
        return ResponseEntity.ok(successResponse(customMessageSource.get("success.retrieve"), list));
    }

    /**
     * this api is to get list of draft share log
     **/
    @GetMapping("/log")
    public ResponseEntity<?> getDraftListLog(@RequestParam(value = "dispatchId", required = false) Long dispatchId,
                                             @RequestParam(value = "memoId", required = false) Long memoId) {
        List<DraftSharePojo> list = draftShareService.getDraftShareLog(dispatchId, memoId);
        return ResponseEntity.ok(successResponse(customMessageSource.get("success.retrieve"), list));
    }

    /**
     * this api is to get list of draft share log
     **/
    @DeleteMapping("/remove-employee")
    public ResponseEntity<?> removeEmployee(
            @RequestParam("pisCode") String pisCode, @RequestParam("sectionCode") String sectionCode,
            @RequestParam(value = "dispatchId", required = false) Long dispatchId,
            @RequestParam(value = "memoId", required = false) Long memoId) {
        boolean result = draftShareService.removeEmployee(pisCode, sectionCode, dispatchId, memoId);
        return ResponseEntity.ok(successResponse(customMessageSource.get("draft.share.remove", dispatchId != null ? customMessageSource.get("dispatch.letter") : customMessageSource.get("memo")), Boolean.valueOf(result)));
    }
}
