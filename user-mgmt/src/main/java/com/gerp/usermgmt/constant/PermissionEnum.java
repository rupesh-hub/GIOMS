package com.gerp.usermgmt.constant;

public enum PermissionEnum {
    MY_OFFICE(IndividualScreenConstants.MY_OFFICE_SCREEN + "_" + ModuleConstants.MY_OFFICE_MODULE);

    private String value;
    PermissionEnum(String value) {
        this.value = value;
    }
}
