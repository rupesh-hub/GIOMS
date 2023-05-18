package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmployeeOfficePojo {
    private Long id;
    private String code;
    private String name;
    private String nameN;
    private String label;
}
