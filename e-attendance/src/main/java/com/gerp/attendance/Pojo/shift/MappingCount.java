package com.gerp.attendance.Pojo.shift;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MappingCount {

    private Long employeeCount;
    private Long groupCount;
    private Boolean status;

    public Boolean getStatus() {
        return (employeeCount==0&&groupCount==0)?false:true;
    }
}
