package com.gerp.usermgmt.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.enums.BloodGroup;
import com.gerp.shared.utils.StringDataUtils;
import com.gerp.usermgmt.enums.RoleLogFlagEnum;
import com.gerp.usermgmt.enums.RoleType;
import lombok.Data;

import java.util.Objects;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleLogDetailPojo {

    private Long id;
    private String name;
    private String Key;
    private String officeCode;

    private RoleType roleType;

    private RoleLogFlagEnum roleLogFlagEnum;


    public void setRoleType(String roleType) {
        if(roleType != null) {
            if (StringDataUtils.isNumeric(roleType)){
                this.roleType = RoleType.getEnumFromCode(Integer.valueOf(roleType));
            } else {
                this.roleType = RoleType.valueOf(roleType);
            }
        }


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
         RoleLogDetailPojo roleLogDetailPojo = (RoleLogDetailPojo) o;
        return Objects.equals(id, roleLogDetailPojo.id) && Objects.equals(Key, roleLogDetailPojo.Key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, Key);
    }







}
