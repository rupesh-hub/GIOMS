package com.gerp.shared.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormValidation {
    public static List<Map<String, Object>> getValidationErrors(BindingResult bindingResult) {
        List<Map<String, Object>> details = new ArrayList<>();

        for (FieldError error : bindingResult.getFieldErrors()) {
            Map<String, Object> errorsMap = new HashMap<>();
            errorsMap.put("fieldName", error.getField());
            errorsMap.put("errorMsg", error.getDefaultMessage());
            details.add(errorsMap);

        }
        return details;
    }
}
