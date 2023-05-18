package com.gerp.usermgmt.pojo.organization.employee;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.enums.PositionType;
import com.gerp.usermgmt.model.employee.Position;
import com.gerp.usermgmt.model.employee.Service;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PositionPojo {
    private String code;

    @NotNull
    private String nameEn;

    private PositionType positionType;

    @NotNull
    private String nameNp;

    @NotNull
    private Long orderNo;
    private String parentCode;

    @JsonIgnore
    public Position getEntity(){
        Position position = new Position();
        position.setCode(this.getCode());
        return position;
    }
}
