package com.gerp.usermgmt.enums;

public enum SectionDesignationActivity {
    ASSIGN_EMPLOYEE_SECTION_DESIGNATION("Assign Employee Section Designation"),
    REMOVE_EMPLOYEE_SECTION_DESIGNATION("Remove Employee Section Designation"),
    UPDATE_EMPLOYEE_SECTION_DESIGNATION("Update Employee Section Designation");

    private final String value;
    SectionDesignationActivity(String value) {
        this.value = value;
    }
}
