package com.gerp.attendance.Pojo.shift.mapped;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.attendance.Pojo.shift.group.ShiftEmployeeGroupPojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShiftMappedResponsePojo {

    private List<ShiftEmployeeGroupPojo> groups;
    private List<EmployeeMinimalPojo> employees;

}
