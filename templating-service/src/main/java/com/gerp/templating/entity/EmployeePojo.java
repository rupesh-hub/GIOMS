package com.gerp.templating.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePojo {

    private String nameNp;
    private String nameEn;
    private FunctionalDesignation functionalDesignation;
}
