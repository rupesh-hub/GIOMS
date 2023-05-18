package com.gerp.attendance.controller;

import com.gerp.shared.enums.*;
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


    @GetMapping("/gender")
    public ResponseEntity<?> gender() {
        List<KeyValuePojo> keyValuePojoList = Gender.getEnumList();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", "Gender"), keyValuePojoList));
    }


    @GetMapping("/gender-with-all")
    public ResponseEntity<?> genderWithAll() {
        List<KeyValuePojo> keyValuePojoList = Gender.getEnumListWithAll();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", "Gender"), keyValuePojoList));
    }

    @GetMapping("/days")
    public ResponseEntity<?> days() {
        List<KeyValuePojo> keyValuePojoList = Day.getEnumList();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", "Day"), keyValuePojoList));
    }

    @GetMapping("/duration-type")
    public ResponseEntity<?> durationType() {
        List<KeyValuePojo> keyValuePojoList = DurationType.getEnumList();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", "Duration Type"), keyValuePojoList));
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        List<KeyValuePojo> keyValuePojoList = Status.getEnumList();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", "Status"), keyValuePojoList));
    }

    @GetMapping("/attendance-type")
    public ResponseEntity<?> attendanceType() {
        List<KeyValuePojo> keyValuePojoList = AttendanceStatus.getEnumList();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get_all", "Attendance Type"), keyValuePojoList));
    }
}
