package com.gerp.usermgmt.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum RoleType {
    SYSTEM,
    ADMIN_CREATE,
    OFFICE_CREATE;


    public static RoleType getEnumFromCode(Integer val){

        switch (val){
            case 0:
                return RoleType.SYSTEM;
            case 1:
                return RoleType.ADMIN_CREATE;
            case 2:
                return RoleType.OFFICE_CREATE;
            default:
                return null;
        }

    }

}
