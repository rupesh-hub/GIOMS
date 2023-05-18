package com.gerp.dartachalani.controller.Enum;

import com.gerp.shared.enums.LetterPriority;
import com.gerp.shared.enums.LetterPrivacy;
import com.gerp.shared.enums.ReceiverType;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.KeyValuePojo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/static")
public class EnumController extends BaseController {

    @GetMapping("/receiver-type-with-all")
    public ResponseEntity<?> receiverTypeWithAll() {
        List<KeyValuePojo> keyValuePojoList = ReceiverType.getEnumList();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", "receiver.type"), keyValuePojoList));
    }

    @GetMapping("/receiver-type")
    public ResponseEntity<?> receiverType() {
        List<KeyValuePojo> keyValuePojoList = ReceiverType.getEnumList();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", "receiver.type"), keyValuePojoList));
    }

    @GetMapping("/letter-priority")
    public ResponseEntity<?> letterPriority() {
        List<KeyValuePojo> keyValuePojoList = LetterPriority.getEnumList();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", "letter.priority"), keyValuePojoList));
    }

    @GetMapping("/letter-privacy")
    public ResponseEntity<?> letterPrivacy() {
        List<KeyValuePojo> keyValuePojoList = LetterPrivacy.getEnumList();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", "letter.privacy"), keyValuePojoList));
    }
}
